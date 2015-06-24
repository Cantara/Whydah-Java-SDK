#!/bin/bash
git remote add upstream https://github.com/cantara/Whydah-Java-SDK.git
git fetch upstream
git merge upstream/master  "sync upstream"
git push
