const fs = require("fs");

exports.preCommit = (props) => {
    const replace = (path, searchValue, replaceValue) => {
        let content = fs.readFileSync(path, "utf-8");
        if (content.match(searchValue)) {
            fs.writeFileSync(path, content.replace(searchValue, replaceValue));
            console.log(`"${path}" changed`);
        }
    };

    const [major, minor, revision] = props.version.split(".");

    replace("./build.gradle", /(?<=major: )\d+/g, major);
    replace("./build.gradle", /(?<=minor: )\d+/g, minor);
    replace("./build.gradle", /(?<=revision: )\d+/g, revision);

    // Find pre-release workflow filename using fs
    const workflow = fs.readdirSync("./.github/workflows").find((file) => file.match(/pre-release/));
    if (workflow) {
        fs.rename(`./.github/workflows/${workflow}`, "./.github/workflows/pre-release-" + props.tag + ".yml", (err) => {
            if (err) {
                console.log(err);
            } else {
                console.log(`".github/workflows/pre-release.yml" renamed`);
            }
        });
    }
};