#!/bin/bash
name="$USER-java"
manager="$USER-manager"
echo undeploy $name

curl -s --netrc-file $HOME/.netrc "https://informatik.hs-bremerhaven.de/$manager/text/undeploy?path=/$name"

