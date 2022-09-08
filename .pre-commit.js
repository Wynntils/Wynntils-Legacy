const fs = require("fs");
const path = require("path");

exports.preCommit = (props) => {
    const replace = (path, searchValue, replaceValue) => {
        let content = fs.readFileSync(path, "utf-8");
        if (content.match(searchValue)) {
            fs.writeFileSync(path, content.replace(searchValue, replaceValue));
            console.log(`"${path}" changed`);
        }
    };

    const replaceAll = (startPath, filter, searchValue, replaceValue) => {
        // thanks: https://stackoverflow.com/questions/25460574/find-files-by-extension-html-under-a-folder-in-nodejs/25462405#25462405
        if (!fs.existsSync(startPath)) return;

        const files = fs.readdirSync(startPath);
        for (var i = 0; i < files.length; i++) {
            const filename = path.join(startPath, files[i]);
            const stat = fs.lstatSync(filename);
            if (stat.isDirectory() && !files[i].match(/^([lL]ib.*)/g)) {
                replaceAll(filename, filter, searchValue, replaceValue); //recurse
            } else if (filename.indexOf(filter) >= 0) {
                replace(filename, searchValue, replaceValue);
            }
        }
    };

    const [major, minor, revision] = props.version.split(".");

    replaceAll("./", "build.gradle", /(?<=major: )\d+/g, major);
    replaceAll("./", "build.gradle", /(?<=minor: )\d+/g, minor);
    replaceAll("./", "build.gradle", /(?<=revision: )\d+/g, revision);
};