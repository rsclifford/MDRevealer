#!/bin/sh
#buildmdrevealer- Compiles MDRevealer, its .po files, and the manual
#License: GPLv3 or later- see GPL.txt for the full license

version=2.25
revision="$(date +%Y%m%d%H%M)"



#echo "Compiling .po files..."
#Spanish
#msgfmt --java2 -d bin/MDRevealer -r MDRevealer -l es po/es.po

#Fix permission errors
cd bin/MDRevealer
chmod 644 *.class
cd ..
cd ..

echo "Dumping strings to po/MDRevealer.po..."
cd src/MDRevealer/
xgettext -k_ --add-comments="TRANSLATORS:" -o ../../po/MDRevealer.po MDRevealer.java call.java mtf.java Resistances.java Output.java Parser.java Organism.java
cd ..
cd ..

echo "Compiling the program..."
rm MDRevealer.jar
ant
cp -f defaultconfig.txt data/config.txt

echo "Compiling the manual..."
cd manual

echo "Making readme.html..."
makeinfo --html --no-split readme.texinfo
mv -f readme.html ..

# PDF makes a buttload of temp files we don't want
#echo "Making readme.pdf..."
#mkdir temp
#cp readme.texinfo temp/readme.texinfo
#cp texinfo.tex temp/texinfo.tex
#cd temp
#makeinfo --pdf readme.texinfo
#mv readme.pdf ..
#cd ..
#rm -rf temp
#mv -f readme.pdf ..
cd ..

echo "Packaging the binary..."
rm -rf output
mkdir output

#Release builds
#mkdir mdrevealer-${version}
#cp -r data output MDRevealer.jar readme.html Apache2.0.txt GPL.txt LGPL.txt NEWS.txt mdrevealer-${version}
#zip -9 -u -r mdrevealer-${version}.zip mdrevealer-${version}
#rm -rf mdrevealer-${version}

#Nightly builds
mkdir mdrevealer-${revision}
cp -r data output MDRevealer.jar readme.html Apache2.0.txt config.txt GPL.txt LGPL.txt NEWS.txt mdrevealer-${revision}
zip -9 -u -r mdrevealer-${revision}.zip mdrevealer-${revision}
rm -rf mdrevealer-${revision}

#Source bundle
echo "Packaging the source..."
mkdir mdrevealer-${revision}-src
cp -r bin data lib manual output po src .classpath .project build.xml config.txt Apache2.0.txt GPL.txt LGPL.txt makemdrevealer.sh NEWS.txt readme.html mdrevealer-${revision}-src
zip -9 -u -r mdrevealer-${revision}-src.zip mdrevealer-${revision}-src
rm -rf mdrevealer-${revision}-src
