package com.wynntils.webapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynntils.Reference;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class WebRequestHandler {
    /**
     * If set to true, will not make HTTP requests.
     */
    public static boolean cacheOnly = false;

    public WebRequestHandler() {}

    public static class Request {

        private String url;
        private String id;
        private int parallelGroup = 0;
        private Predicate<byte[]> handler;
        private File cacheFile;
        private int currentlyHandling = 0;
        private int timeout = 16000;

        public Request(String url, String id) {
            this.url = url;
            this.id = id;
        }

        /**
         * Sets the parallel group.
         * Requests in the same parallel group will be requested at the
         * same time. Greater parallel groups will be requested after smaller ones.
         */
        public Request withParallelGroup(int group) {
            this.parallelGroup = group;
            return this;
        }

        /**
         * Callback called with raw bytes from request.
         *
         * If it returns true, the data is marked as good (Will be cached if there is a cache file).
         * If false or throws, the data is marked as bad (Uses cache file if this was a request, deletes cache file if cache was bad)
         */
        public Request handle(Predicate<byte[]> handler) {
            this.handler = handler;
            return this;
        }

        /**
         * As {@link #handle(Predicate<byte[]>) handle}, but the data is converted into a String first using Charset
         */
        public Request handleString(Predicate<String> handler, Charset charset) {
            return handle(data -> handler.test(new String(data, charset)));
        }

        /**
         * As {@link #handle(Predicate<byte[]>) handle}, but the data is interpreted as UTF-8
         */
        public Request handleString(Predicate<String> handler) {
            return handleString(handler, StandardCharsets.UTF_8);
        }

        /**
         * As {@link #handle(Predicate<byte[]>) handle}, but the data is parsed as JSON
         */
        public Request handleJson(Predicate<JsonElement> handler) {
            return handleString(s -> handler.test(new JsonParser().parse(s)));
        }

        /**
         * As {@link #handle(Predicate<byte[]>) handle}, but the data is parsed as JSON and converted into an Object
         */
        public Request handleJsonObject(Predicate<JsonObject> handler) {
            return handleJson(j -> {
                if (!j.isJsonObject()) return false;
                return handler.test(j.getAsJsonObject());
            });
        }

        /**
         * Sets the cache file. Good data will be written here, and if there is no good data, it will be read from here.
         */
        public Request cacheTo(File f) {
            this.cacheFile = f;
            return this;
        }

        /**
         * Timeout length for requests
         */
        public Request setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
    }

    private ExecutorService pool = Executors.newFixedThreadPool(4);
    private ArrayList<Request> requests = new ArrayList<>();
    private int maxParallelGroup = 0;
    private int dispatchId = 0;

    /**
     * Enqueue a new {@link Request Request}
     */
    public void addRequest(Request req) {
        synchronized (this) {
            for (Request request : requests) {
                if (req.id.equals(request.id)) {
                    return;
                }
            }
            requests.add(req);

            if (req.parallelGroup > maxParallelGroup) {
                maxParallelGroup = req.parallelGroup;
            }
        }
    }

    /**
     * Send all enqueued requests and wait until complete
     */
    public void dispatch() {
        dispatch(false);
    }

    /**
     * Send all enqueued requests inside of a new thread and return that thread
     */
    public Thread dispatchAsync() {
        return dispatch(true);
    }

    private Thread dispatch(boolean async) {
        ArrayList<Request>[] groupedRequests;
        boolean anyRequests = false;
        int thisDispatch;

        synchronized (this) {
            groupedRequests = (ArrayList<Request>[]) new ArrayList[maxParallelGroup + 1];

            for (int i = 0; i < maxParallelGroup + 1; ++i) {
                groupedRequests[i] = new ArrayList<>();
            }

            for (Request request : requests) {
                if(request.currentlyHandling != 0) continue;

                anyRequests = true;
                request.currentlyHandling = 1;
                groupedRequests[request.parallelGroup].add(request);
            }

            maxParallelGroup = 0;
            thisDispatch = ++dispatchId;
        }

        if (anyRequests) {
            if (!async) {
                handleDispatch(thisDispatch, groupedRequests, 0);
                return null;
            }

            Thread t = new Thread(() -> handleDispatch(thisDispatch, groupedRequests, 0));
            t.start();
            return t;
        }

        return null;
    }

    private void handleDispatch(int dispatchId, ArrayList<Request>[] groupedRequests, int currentGroupIndex) {
        ArrayList<Request> currentGroup = groupedRequests[currentGroupIndex];
        if (currentGroup.size() == 0) {
            nextDispatch(dispatchId, groupedRequests, currentGroupIndex);
            return;
        }
        ArrayList<Callable<Void>> tasks = new ArrayList<>(requests.size());
        for (Request req : requests) {
            tasks.add(() -> {
                byte[] toCache = null;
                if (req.url != null && !cacheOnly) {
                    Throwable readException = null;
                    try {
                        URLConnection st = new URL(req.url).openConnection();
                        st.setRequestProperty("User-Agent", "WynntilsClient v" + Reference.VERSION + "/B" + Reference.BUILD_NUMBER);
                        st.setConnectTimeout(req.timeout);
                        st.setReadTimeout(req.timeout);
                        byte[] data;
                        try {
                            data = IOUtils.toByteArray(st.getInputStream());
                        } catch (IOException e) {
                            readException = e;
                            throw e;
                        }
                        if (req.handler.test(data)) {
                            toCache = data;
                        } else {
                            Reference.LOGGER.info("Error occurred whilst fetching " + req.id + " from " + req.url + ": Invalid data received" + (req.cacheFile == null ? "" : "; Attempting to use cache"));
                        }
                    } catch (Exception e) {
                        if (readException != null) {
                            Reference.LOGGER.info("Error occurred whilst fetching " + req.id + " from " + req.url + ": " + (e instanceof SocketTimeoutException ? "Socket timeout (server may be down)" : e.getMessage()) + (req.cacheFile == null ? "" : "; Attempting to use cache"));
                        } else {
                            Reference.LOGGER.info("Error occurred whilst fetching " + req.id + " from " + req.url + (req.cacheFile == null ? "" : "; Attempting to use cache"));
                            e.printStackTrace();
                        }
                    }
                }
                if (req.cacheFile != null) {
                    if (toCache != null) {
                        try {
                            FileUtils.writeByteArrayToFile(req.cacheFile, toCache);
                        } catch (Exception e) {
                            Reference.LOGGER.info("Error occurred whilst writing cache for " + req.id);
                            e.printStackTrace();
                            moveInvalidCache(req.cacheFile);
                        }
                    } else {
                        try {
                            if (!req.handler.test(FileUtils.readFileToByteArray(req.cacheFile))) {
                                Reference.LOGGER.info("Error occurred whilst trying to use cache for " + req.id + " at " + req.cacheFile.getPath() + ": Cache file is invalid");
                                moveInvalidCache(req.cacheFile);
                            }
                        } catch (Exception e) {
                            Reference.LOGGER.info("Error occurred whilst trying to use cache for " + req.id + " at " + req.cacheFile.getPath());
                            e.printStackTrace();
                            moveInvalidCache(req.cacheFile);
                        }
                    }
                }
                req.currentlyHandling = 2;
                return null;
            });
        }
        boolean interrupted = false;
        try {
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            interrupted = true;
        }
        if (interrupted) {
            HashSet<String> completedIds = new HashSet<>();
            HashSet<String> interruptedIds = new HashSet<>();
            for (ArrayList<Request> requests : groupedRequests) for (Request request : requests) {
                (request.currentlyHandling == 2 ? completedIds : interruptedIds).add(request.id);
            }
            synchronized (this) {
                requests.removeIf(req -> {
                    if (completedIds.contains(req.id)) {
                        return true;
                    }
                    if (interruptedIds.contains(req.id)) {
                        req.currentlyHandling = 0;
                    }
                    return false;
                });
            }

            return;
        }

        nextDispatch(dispatchId, groupedRequests, currentGroupIndex);
    }

    private static void moveInvalidCache(File from) {
        File invalid = new File(from.getAbsolutePath() + ".invalid");
        FileUtils.deleteQuietly(invalid);

        try {
            from.renameTo(invalid);
        } catch (Exception ignore) {}
    }

    private void nextDispatch(int dispatchId, ArrayList<Request>[] groupedRequests, int currentGroupIndex) {
        if (currentGroupIndex != groupedRequests.length - 1) {
            handleDispatch(dispatchId, groupedRequests, currentGroupIndex + 1);
            return;
        }

        // Last group; Remove handled requests
        HashSet<String> ids = new HashSet<>();
        for (ArrayList<Request> requests : groupedRequests) for (Request request : requests) {
            ids.add(request.id);
        }
        synchronized (this) {
            requests.removeIf(req -> ids.contains(req.id));
        }
    }

}
