#!/bin/bash

PROJ_ID=serverpanelmanager
PROJ_NAME=ServerPanelManager
VERSION=1.0.3
GROUP_ID=dev/neuralnexus

# --------------------------- Functions --------------------------------

function prepareFiles() {
  # Prepare PLATFORM files
  cp ../$PROJ_NAME-$VERSION-$1.jar ./
  mv ./$PROJ_NAME-$VERSION-$1.jar ./$PROJ_NAME-$VERSION-$1.zip
  unzip ./$PROJ_NAME-$VERSION-$1.zip -d ./$1
  rm -rf ./$PROJ_NAME-$VERSION-$1.zip
}

function build() {
  mkdir -p ./$3

  # Copy common files
  cp -r ./$PROJ_NAME-all/* ./$3/

  # Copy fabric files
  cp -r ./fabric-$1/$GROUP_ID/$PROJ_ID/fabric ./$3/$GROUP_ID/$PROJ_ID
  cp ./fabric-$1/fabric.mod.json ./$3
  cp ./fabric-$1/$PROJ_ID.mixins.json ./$3
  cp -r ./fabric-$1/assets ./$3
  cp ./fabric-$1/fabric-$1-refmap.json ./$3

  # Copy forge files
  cp -r ./forge-$2/$GROUP_ID/$PROJ_ID/forge ./$3/$GROUP_ID/$PROJ_ID
  cp ./forge-$2/pack.mcmeta ./$3
  cp -r ./forge-$2/$PROJ_NAME.png ./$3
  mkdir -p ./$3/META-INF
  cp ./forge-$2/META-INF/mods.toml ./$3/META-INF

  # Zip Jar contents
  cd ./$3
  zip -r ../$3.zip ./*
  cd ../

  # Rename Jar
  mv ./$3.zip ./$3.jar

  # Generate MD5
  md5sum ./$3.jar | cut -d ' ' -f 1 > ./$3.jar.MD5

  # Move Jar
  mv ./$3.jar ../$3.jar
  mv ./$3.jar.MD5 ../$3.jar.MD5
}

# --------------------------- Setup --------------------------------

# Make directories
mkdir -p ./target/temp_build
cd ./target/temp_build

mkdir -p ./$PROJ_NAME-all/$GROUP_ID/$PROJ_ID

# --------------------------- Prepare Common --------------------------------

# Prepare bukkit files
prepareFiles bukkit

# Copy bukkit files
mv ./bukkit/$GROUP_ID/$PROJ_ID/bukkit ./$PROJ_NAME-all/$GROUP_ID/$PROJ_ID
cp ./bukkit/plugin.yml ./$PROJ_NAME-all
rm -rf ./bukkit

# Prepare bungee files
prepareFiles bungee

# Copy bungee files
mv ./bungee/$GROUP_ID/$PROJ_ID/bungee ./$PROJ_NAME-all/$GROUP_ID/$PROJ_ID
cp ./bungee/bungee.yml ./$PROJ_NAME-all
rm -rf ./bungee

# Prepare velocity files
prepareFiles velocity

# Copy velocity files
mv ./velocity/$GROUP_ID/$PROJ_ID/velocity ./$PROJ_NAME-all/$GROUP_ID/$PROJ_ID
cp ./velocity/velocity.yml ./$PROJ_NAME-all/$GROUP_ID/$PROJ_ID
cp ./velocity/velocity-plugin.json ./$PROJ_NAME-all
rm -rf ./velocity

# Prepare common files
prepareFiles common

# Copy common files
mv ./common/$GROUP_ID/$PROJ_ID/common ./$PROJ_NAME-all/$GROUP_ID/$PROJ_ID
mv ./common/$GROUP_ID/$PROJ_ID/lib ./$PROJ_NAME-all/$GROUP_ID/$PROJ_ID
cp ./common/config.yml ./$PROJ_NAME-all
cp ./common/LICENSE ./$PROJ_NAME-all
cp ../../LICENSE-API ./$PROJ_NAME-all
cp ../../README.md ./$PROJ_NAME-all
rm -rf ./common

# Prepare standalone files
prepareFiles standalone

# Copy standalone files
mv ./standalone/$GROUP_ID/$PROJ_ID/standalone ./$PROJ_NAME-all/$GROUP_ID/$PROJ_ID
mkdir -p ./$PROJ_NAME-all/META-INF
echo "Manifest-Version: 1.0" > ./$PROJ_NAME-all/META-INF/MANIFEST.MF
echo "Main-Class: dev.neuralnexus.serverpanelmanager.standalone.StandaloneMain" >> ./$PROJ_NAME-all/META-INF/MANIFEST.MF
rm -rf ./standalone

# --------------------------- Prepare Forge and Fabric --------------------------------

# Prepare Fabric 1.14 files
FABRIC_VERSION=1.14
prepareFiles fabric-$FABRIC_VERSION

# Prepare Fabric 1.15 files
FABRIC_VERSION=1.15
prepareFiles fabric-$FABRIC_VERSION

# Prepare Fabric 1.16 files
FABRIC_VERSION=1.16
prepareFiles fabric-$FABRIC_VERSION

# Prepare Fabric 1.17 files
FABRIC_VERSION=1.17
prepareFiles fabric-$FABRIC_VERSION

# Prepare Fabric 1.20 files
FABRIC_VERSION=1.20
prepareFiles fabric-$FABRIC_VERSION

# Prepare Forge 1.12 files
FORGE_VERSION=1.12
prepareFiles forge-$FORGE_VERSION

# Prepare Forge 1.13 files
FORGE_VERSION=1.13
prepareFiles forge-$FORGE_VERSION

# Prepare Forge 1.14 files
FORGE_VERSION=1.14
prepareFiles forge-$FORGE_VERSION

# Prepare Forge 1.15 files
FORGE_VERSION=1.15
prepareFiles forge-$FORGE_VERSION

# Prepare Forge 1.16 files
FORGE_VERSION=1.16
prepareFiles forge-$FORGE_VERSION

# Prepare Forge 1.17 files
FORGE_VERSION=1.17
prepareFiles forge-$FORGE_VERSION

# Prepare Forge 1.18 files
FORGE_VERSION=1.18
prepareFiles forge-$FORGE_VERSION

# Prepare Forge 1.19 files
FORGE_VERSION=1.19
prepareFiles forge-$FORGE_VERSION

# Prepare Forge 1.20 files
FORGE_VERSION=1.20
prepareFiles forge-$FORGE_VERSION

# --------------------------- Build 1.12 --------------------------------
MC_VERSION=1.12
FABRIC_VERSION=NA
FORGE_VERSION=1.12
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Build 1.13 --------------------------------
MC_VERSION=1.13
FABRIC_VERSION=NA
FORGE_VERSION=1.13
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Build 1.14 --------------------------------
MC_VERSION=1.14
FABRIC_VERSION=1.14
FORGE_VERSION=1.14
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Build 1.15 --------------------------------
MC_VERSION=1.15
FABRIC_VERSION=1.15
FORGE_VERSION=1.15
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Build 1.16 --------------------------------
MC_VERSION=1.16
FABRIC_VERSION=1.16
FORGE_VERSION=1.16
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Build 1.17 --------------------------------
MC_VERSION=1.17
FABRIC_VERSION=1.17
FORGE_VERSION=1.17
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Build 1.18 --------------------------------
MC_VERSION=1.18
FABRIC_VERSION=1.17
FORGE_VERSION=1.18
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Build 1.19 --------------------------------
MC_VERSION=1.19
FABRIC_VERSION=1.17
FORGE_VERSION=1.19
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Build 1.20 --------------------------------
MC_VERSION=1.20
FABRIC_VERSION=1.20
FORGE_VERSION=1.20
OUT_FILE=$PROJ_NAME-$VERSION-$MC_VERSION

build $FABRIC_VERSION $FORGE_VERSION $OUT_FILE

# --------------------------- Cleanup --------------------------------
cd ../
rm -rf temp_build
