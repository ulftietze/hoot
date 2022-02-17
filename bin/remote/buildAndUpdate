#!/bin/bash
name="$USER-java"
manager="$USER-manager"

# tomcat available?
if ! curl -f -s -o /dev/null https://informatik.hs-bremerhaven.de/$manager/ ; then 
   echo "Ihr tomcat ist nicht aktiviert. Aktivieren Sie auf hopper mit"
   echo "  hbv_dockertomcatstarten"
   echo "zuerst Ihren tomcat."
   exit 1
fi

echo compile
mkdir -p build
cp -r app/* build
export CLASSPATH=.:$(find complibs -name '*jar'|tr '\n' ':')
javafiles=$(find src -name '*java')
javac -d build/WEB-INF/classes $javafiles

echo build the war-file
jar -cf $name.war -C build .

ls $name.war

echo deploy as $name
curl -s -n -T $name.war "https://informatik.hs-bremerhaven.de/$manager/text/deploy?path=/$name&update=true"
curl -s https://informatik.hs-bremerhaven.de/$name/hello|html2text

