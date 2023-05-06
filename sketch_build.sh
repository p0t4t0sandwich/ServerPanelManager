#!/bin/bash

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

cp ../standalone/build/libs/standalone-*.jar ./
mv ./standalone-*.jar ./standalone.zip

# Unzip Jars
unzip ./api.zip -d ./api
unzip ./bukkit.zip -d ./bukkit
unzip ./bungee.zip -d ./bungee
unzip ./common.zip -d ./common
unzip ./fabric.zip -d ./fabric
unzip ./standalone.zip -d ./standalone

# Process Jars
cp -r ./api/ca/sperrer/p0t4t0sandwich/panelservermanager/api ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/api
cp -r ./bukkit/ca/sperrer/p0t4t0sandwich/panelservermanager/bukkit ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/bukkit
cp ./bukkit/plugin.yml ./panelservermanager
cp -r ./bungee/ca/sperrer/p0t4t0sandwich/panelservermanager/bungee ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/bungee
cp ./bungee/bungee.yml ./panelservermanager
cp -r ./common/* ./panelservermanager
rm -f ./panelservermanager/LICENSE
cp -r ./fabric/ca/sperrer/p0t4t0sandwich/panelservermanager/fabric ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/fabric
cp -r ./standalone/ca/sperrer/p0t4t0sandwich/panelservermanager/standalone ./panelservermanager/ca/sperrer/p0t4t0sandwich/panelservermanager/standalone
rm -rf ./panelservermanager/META-INF/*
cp ./standalone/META-INF/MANIFEST.MF ./panelservermanager/META-INF/MANIFEST.MF

# Zip Jar contents
cd ./panelservermanager
zip -r ./panelservermanager.zip ./*

# Rename Jar
mv ./panelservermanager.zip ../../build/libs/panelservermanager.jar
