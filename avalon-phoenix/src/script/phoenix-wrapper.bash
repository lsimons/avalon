#! /bin/bash

#
# Skeleton bash script suitable for starting and stopping 
# wrapped Java apps on the Linux platform.
#
# This script expects to find the 'realpath' executable
# in the same directory. 
#

#-----------------------------------------------------------------------------
# These settings can be modified to fit the needs of your application

# Application
APP_NAME="phoenix"
APP_LONG_NAME="Phoenix Application Server"

# Wrapper
WRAPPER_CMD="./wrapper"
WRAPPER_CONF="../conf/wrapper.conf"

# Priority (see the start() method if you want to use this)
PRIORITY=

# Do not modify anything beyond this point
#-----------------------------------------------------------------------------

# Get to the actual location of this script
SCRIPT_DIR=`dirname $0`
SCRIPT=`$SCRIPT_DIR/realpath $0`
cd `dirname $SCRIPT`

# Find pidof.
PIDOF="/bin/pidof"
if [ ! -x $PIDOF ]
then
    PIDOF="/sbin/pidof"
    if [ ! -x $PIDOF ]
    then
        echo "Cannot find 'pidof' in /bin or /sbin."
        echo "This script requires 'pidof' to run."
        exit 1
    fi
fi

console() {
    echo "Running $APP_LONG_NAME..."
    pid=`$PIDOF $APP_NAME`
    if [ -z $pid ]
    then
        # If you wanted to specify the priority with which
        # your app runs, you could use nice here:
        # exec -a $APP_NAME nice -$PRIORITY $WRAPPER_CMD $WRAPPER_CONF
        # See "man nice" for more details.
        exec -a $APP_NAME $WRAPPER_CMD $WRAPPER_CONF
    else
        echo "$APP_LONG_NAME is already running."
        exit 1
    fi
}

start() {
    echo "Starting $APP_LONG_NAME..."
    pid=`$PIDOF $APP_NAME`
    if [ -z $pid ]
    then
        # If you wanted to specify the priority with which
        # your app runs, you could use nice here:
        # exec -a $APP_NAME nice -$PRIORITY $WRAPPER_CMD $WRAPPER_CONF wrapper.daemonize=TRUE
        # See "man nice" for more details.
        exec -a $APP_NAME $WRAPPER_CMD $WRAPPER_CONF wrapper.daemonize=TRUE
    else
        echo "$APP_LONG_NAME is already running."
        exit 1
    fi
}

stop() {
    echo "Stopping $APP_LONG_NAME..."
    pid=`$PIDOF $APP_NAME`
    if [ -z $pid ]
    then
        echo "$APP_LONG_NAME was not running."
    else
        kill $pid
        sleep 6

        pid=`$PIDOF $APP_NAME`
        if [ ! -z $pid ]
        then
            kill -9 $pid
        fi

        pid=`$PIDOF $APP_NAME`
        if [ ! -z $pid ]
        then
            echo "Failed to stop $APP_LONG_NAME."
        else
            echo "Stopped $APP_LONG_NAME."
        fi
    fi
}

dump() {
    echo "Dumping $APP_LONG_NAME..."
    pid=`$PIDOF $APP_NAME`
    if [ -z $pid ]
    then
        echo "$APP_LONG_NAME was not running."
    else
        kill -3 $pid

        if [ $? -ne 0 ]
        then
            echo "Failed to dump $APP_LONG_NAME."
        else
            echo "Dumped $APP_LONG_NAME."
        fi
    fi
}

case "$1" in

    'console')
        console
        ;;

    'start')
        start
        ;;

    'stop')
        stop
        ;;

    'restart')
        stop
        start
        ;;

    'dump')
        dump
        ;;

    *)
        echo "Usage: $0 { console | start | stop | restart | dump }"
        exit 1
        ;;
esac

exit 0
