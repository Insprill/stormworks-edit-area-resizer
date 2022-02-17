import os
import re
import time
from os import listdir
from os.path import isfile, join

path = input("Enter the installation path of StormWorks: ")
if path == "default":
    path = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Stormworks"

path += "/rom/data/tiles"

if not os.path.isdir(path):
    print(f"Could not find tiles directory at '{path}'.")
    exit(1)

size = int(input("Enter the new size of all edit areas (1-255): "))
size = max(1, min(size, 255))

startTime = time.time()

sizePattern = re.compile(r"(\d*\.)?\d+")
gridSizePattern = re.compile(r"(?<=grid_size=\")((\d*\.)?\d+)")

for fileName in [f for f in listdir(path) if isfile(join(path, f))]:
    if not fileName.endswith(".xml"):
        continue
    print("Modifying " + fileName)

    inEditArea = False
    finishedEdit = False

    fileText = ""

    for line in open(f"{path}/{fileName}", 'r').readlines():
        trimmed = line.strip()
        if not finishedEdit and trimmed.startswith("<edit_areas>"):
            inEditArea = True
        elif inEditArea and trimmed.startswith("</edit_areas>"):
            inEditArea = False
            finishedEdit = True

        if inEditArea and trimmed.startswith("<size "):
            line = sizePattern.sub(str(size), line)
        elif inEditArea and trimmed.startswith("<edit_area "):
            line = gridSizePattern.sub(str(size), line)

        fileText += line

    open(f"{path}/{fileName}", 'w').write(fileText)

elapsedTime = round((time.time() - startTime) * 1000)
print(f"Finished in {elapsedTime} ms!")
