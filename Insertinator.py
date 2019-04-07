import sys, os
path = os.path.dirname(os.path.abspath(sys.argv[0])) + "/src/main/resources/assets/wynntils/lang/"
f = open(path + "en_us.lang", "r")
en_us_data = f.readlines()
f.close()

for filename in os.listdir(path):
    if (filename.endswith(".lang")):
        if (filename.endswith("en_us.lang")):
            continue

        f = open(os.path.dirname(path) + "/" + filename, "r")
        other_lang_data = f.readlines()
        f.close()
        inserts = 0
        appends = 0
        for i in range(len(en_us_data)):
            try:
                if (en_us_data[i].split("=")[0] != other_lang_data[i].split("=")[0] and not (en_us_data[i].startswith("#"))):
                    inserts += 1
                    other_lang_data.insert(i, "{0} ##TODO\n".format(en_us_data[i].strip()))
            except IndexError:
                appends += 1
                other_lang_data.append("{0} ##TODO\n".format(en_us_data[i].strip()))
        f = open(os.path.dirname(path) + "/" + filename, "w")
        f.writelines(other_lang_data)
        f.close()
        print("Finished {0}, Inserted {1} line(s) and Appended {2} line(s)".format(filename, inserts, appends))