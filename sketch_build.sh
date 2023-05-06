#!/bin/bash

VERSION=1.0.0
MC_VERSION=1.19.4

# Make directories
rm -rf ./temp_build
mkdir -p ./temp_build
cd temp_build

rm -rf ./panelservermanager
mkdir -p ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager

# Copy output Jars to temp directory
cp ../api/build/libs/api-*.jar ./
mv ./api-*.jar ./api.zip

cp ../bukkit/build/libs/bukkit-*.jar ./
mv ./bukkit-*.jar ./bukkit.zip

cp ../bungee/build/libs/bungee-*.jar ./
mv ./bungee-*.jar ./bungee.zip

cp ../common/build/libs/common-*.jar ./
mv ./common-*.jar ./common.zip

cp ../fabric/build/libs/fabric-*.jar ./
mv ./fabric-*.jar ./fabric.zip

cp ../forge/build/libs/forge-*.jar ./
mv ./forge-*.jar ./forge.zip

cp ../standalone/build/libs/standalone-*.jar ./
mv ./standalone-*.jar ./standalone.zip

# Unzip Jars
unzip ./api.zip -d ./api
unzip ./bukkit.zip -d ./bukkit
unzip ./bungee.zip -d ./bungee
unzip ./common.zip -d ./common
unzip ./fabric.zip -d ./fabric
unzip ./forge.zip -d ./forge
unzip ./standalone.zip -d ./standalone

# Process Jars
cp -r ./api/ca/sperrer/p0t4t0sandwich/panelservermanager/api ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/api

cp -r ./bukkit/ca/sperrer/p0t4t0sandwich/panelservermanager/bukkit ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/bukkit
cp ./bukkit/plugin.yml ./panelservermanager

cp -r ./bungee/ca/sperrer/p0t4t0sandwich/panelservermanager/bungee ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/bungee
cp ./bungee/bungee.yml ./panelservermanager

cp -r ./common/* ./panelservermanager
rm -rf ./panelservermanager/META-INF/*
rm -f ./panelservermanager/LICENSE

cp -r ./fabric/ca/sperrer/p0t4t0sandwich/panelservermanager/fabric ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/fabric
cp ./fabric/fabric.mod.json ./panelservermanager
cp ./fabric/panelservermanager.mixins.json ./panelservermanager
cp -r ./fabric/assets ./panelservermanager

cp -r ./forge/ca/sperrer/p0t4t0sandwich/panelservermanager/forge ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/forge
cp ./forge/pack.mcmeta ./panelservermanager
cp ./forge/META-INF/mods.toml ./panelservermanager/META-INF

cp -r ./standalone/ca/sperrer/p0t4t0sandwich/panelservermanager/standalone ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/standalone
echo "Manifest-Version: 1.0" > ./panelservermanager/META-INF/MANIFEST.MF
echo "Main-Class: ca.sperrer.p0t4t0sandwich.panelservermanager.standalone.StandaloneMain" >> ./panelservermanager/META-INF/MANIFEST.MF

# Zip Jar contents
cd ./panelservermanager
zip -r ./panelservermanager.zip ./*

# Rename Jar
mv ./panelservermanager.zip ../../build/libs/PanelServerManager-$VERSION-$MC_VERSION.jar
