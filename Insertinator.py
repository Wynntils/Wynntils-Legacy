import sys, os
path = os.path.dirname(os.path.abspath(sys.argv[0])) + "/src/main/resources/assets/wynntils/lang/"
f = open(path + "en_us.lang", "r", encoding='utf-8')
en_us_data = f.readlines()
f.close()

for filename in os.listdir(path):
    if (filename.endswith(".lang")):
        if (filename.endswith("en_us.lang")):
            continue

        f = open(os.path.dirname(path) + "/" + filename, "r", encoding='utf-8')
        other_lang_data = f.readlines()
        f.close()
        inserts = 0
        appends = 0
        for i in range(len(en_us_data)):
            try:
                if (en_us_data[i].split("=")[0] != other_lang_data[i].split("=")[0] and (not en_us_data[i].startswith("#"))):
                    inserts += 1
                    #print("{0} inserted at {1}".format(en_us_data[i], i))
                    if (len(en_us_data[i].strip()) == 0 or ("#" in en_us_data[i])):
                        other_lang_data.insert(i, en_us_data[i])
                    else:
                        other_lang_data.insert(i, "{0} ##TODO\n".format(en_us_data[i].strip()))
                elif ((en_us_data[i].strip().startswith("#") and en_us_data[i].strip().endswith("#")) and en_us_data[i] != other_lang_data[i]):
                    other_lang_data.insert(i, en_us_data[i])
            except IndexError:
                appends += 1
                #print("{0} appended at {1}".format(en_us_data[i], i))
                if (len(en_us_data[i].strip()) == 0 or ("#" in en_us_data[i])):
                    other_lang_data.append(en_us_data[i])
                else:
                    other_lang_data.append("{0} ##TODO\n".format(en_us_data[i].strip()))
        f = open(os.path.dirname(path) + "/" + filename, "w", encoding='utf-8')
        f.writelines(other_lang_data)
        f.close()
        print("Finished {0}, Inserted {1} line(s) and Appended {2} line(s)".format(filename, inserts, appends))