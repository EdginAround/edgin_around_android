#!/bin/sh

JNI_DIR="edgin_around/src/main/jniLibs"
EDGIN_AROUND_VERSION="0.1.1"
EDGIN_AROUND_DOWNLOADS="https://github.com/EdginAround/edgin_around_rendering/releases/download/"
EDGIN_AROUND_PACKAGE="edgin_around_android.zip"
EDGIN_AROUND_RELEASE="$EDGIN_AROUND_DOWNLOADS/$EDGIN_AROUND_VERSION/$EDGIN_AROUND_PACKAGE"

function usage() {
    echo 'Commands:'
    echo ' - setup - automates setting the build up'
    echo ' - build - builds Android app'
    echo ' - test - runs unit tests'
    echo ' - logcat - shows relevant logs from a connected device'
    echo ' - format - runs `ktlint` code liter and formatter'
}

function run_setup() {
    rm -rf $JNI_DIR && mkdir -p $JNI_DIR && \
    cd $JNI_DIR && \
    wget $EDGIN_AROUND_RELEASE && \
    unzip $EDGIN_AROUND_PACKAGE && \
    rm $EDGIN_AROUND_PACKAGE && \
    cd -
}

function run_check_setup() {
    if !test -d $JNI_DIR; then
        echo "Run setup first"
        echo
        usage
        exit 1
    fi
}

function run_build() {
    gradle build
}

function run_test() {
    gradle test
}

function run_format() {
    gradle ktlintFormat
}

function run_logcat() {
    adb logcat *:S AndroidRuntime:E ActivityManager:I EdginAround:*
}

function run_clean() {
    rm -rf $JNI_DIR
    gradle clean
}

if (( $# > 0 )); then
    command=$1
    shift

    case $command in
        'setup')
            run_setup
            ;;
        'build')
            run_check_setup
            run_build
            ;;
        'test')
            run_test
            ;;
        'format')
            run_format
            ;;
        'logcat')
            run_logcat
            ;;
        'clean')
            run_clean
            ;;
        *)
            echo "Command \"$command\" unknown."
            echo
            usage
            ;;
    esac
else
    echo 'Please give a command.'
    echo
    usage
fi

