const fs = require("fs");

exports.preCommit = (props) => {
    const replace = (path, searchValue, replaceValue) => {
        let content = fs.readFileSync(path, "utf-8");
        if (content.match(searchValue)) {
            fs.writeFileSync(path, content.replace(searchValue, replaceValue));
            console.log(`"${path}" changed`);
        }
    };

    const [major, minor, patch, build] = props.version.split(".");
    let revision, identifier;
    if (props.version.includes("-")) { // e.g. v1.12.1-beta.6
        [revision, identifier] = patch.split("-");
    } else { // e.g. v1.12.1
        revision = patch;
        identifier = "";
    }

    replace("./build.gradle", /(?<=major: )\d+/g, major);
    replace("./build.gradle", /(?<=minor: )\d+/g, minor);
    replace("./build.gradle", /(?<=revision: )\d+/g, revision);
};