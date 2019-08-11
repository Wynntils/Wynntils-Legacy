/*Written by douira (see GitHub),
requires Node.js 12.0.0 or higher to run
example: node format-GT-lang-files.js de_de

This script is supposed to be used after passing the english
source file through Google Translate file translation.
(you might need to split the file into pieces)
It's still necessary to translate manually,
GT and this script just make it a little less boring
*/
console.log("Starting...");

//get the file library
const fs = require("fs");

//figure out where to work
const workDir = __dirname + "/src/main/resources/assets/wynntils/lang";

//check what language file should be worked on
const targetLang = process.argv[2];

//load the lang files and split by lines, trim to make newlines consistent
console.log("Loading Files...");
const original = fs
  .readFileSync(workDir + "/en_us.lang", "utf8")
  .trim()
  .split("\n");
const translated = fs
  .readFileSync(`${workDir}/${targetLang}.lang`, "utf8")
  .trim()
  .split("\n");

//track progress
let lastProgress = -1;

/*the regex used for matching special codes together with spacing
these are found in the original and the translation,
then the instances in the translation are replaced with
the instances from the original at the same count index,
explanation at https://regex101.com/r/vq5Xuk, choose the latest saved version*/
const specialCodesRegex = /(?:^|["' ([\{])\/\w+ ?| ?(?<!%)(?:§\w|% ?\w|\$ ?\w|\( ?.? ?\/ ?.? ?\)|\\+(?!n)|[<>\[\]|:+\/\(\)]) ?(?!\/\w+)/gu;

//format the file line by line
console.log("Formatting Lines...");
const formatted = translated.map((trans, index) => {
  //compute progress
  const progress = Math.round((index / translated.length) * 100);

  //if at interval
  if (lastProgress === -1 || progress - lastProgress >= 10) {
    //log progress
    console.log(`Progress ${progress}%`);

    //and save to prevent from logging within this interval
    lastProgress = progress;
  }

  //get the equivalent line in the original file,
  //this expects the files to have matching lines
  const orig = original[index];

  //if the original line doesn't start with wynntils this is something special and should not be changed
  if (!orig.startsWith("wynntils")) {
    return orig;
  }

  //get the content part of the original
  const origContent = orig.substring(orig.indexOf("=") + 1);

  //catch problems
  try {
    //if the translation contains a malformed unicode escape
    if (trans.search(/\\ *u00/gu) !== -1) {
      //throw error, original is used
      throw Error("Malformed unicode escape found");
    }

    //find the special codes in the original string in the content
    //match all instances of a special code including the spacing
    const origSpecialCodes = origContent.match(specialCodesRegex);

    //keep track of the current inserted special code
    let specialCodeIndex = 0;

    //do some formatting work
    trans = trans
      .substring(trans.indexOf("=") + 1)

      //trim extra whitespace including weird whitespace
      .trim()
      .replace(/\u200b/gu, "")

      //make the newlines normal
      .replace(/ *\\ *[Nn] */gu, "\\n")

      //find special codes and replace with correct spacing
      .replace(specialCodesRegex, () => {
        //throw if doesn't exist
        if (!origSpecialCodes || !origSpecialCodes[specialCodeIndex]) {
          //throw warning about mismatch
          throw Error("Special codes match amount mismatch");
        }

        //return the matched string to be preserved
        return origSpecialCodes[specialCodeIndex++];
      });
  } catch (err) {
    //on error use the original line completely
    trans = origContent;

    //log thrown error
    console.warn(
      `WARN: ${err.message} on line ${index + 1}, falling back to original`
    );
  }

  //use the original key and the formatted translation
  return orig.substring(0, orig.indexOf("=") + 1) + trans;
});

//write out into new file in same dir
console.log("Saving...");
fs.writeFileSync(
  `${workDir}/${targetLang}-formatted.lang`,
  formatted.join("\n")
);

console.log("Done!");
