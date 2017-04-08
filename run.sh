
# Runs the appropriate make in Bash Shell

# arguement is the target

export LD_LIBRARY_PATH=""
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.

if [ "$1" == "clean" ];
then
	make clean
fi

if [ "$1" == "mac" ];
then
    echo "----- Compiling code for Mac -----"
    make mac
fi

if [ "$1" == "linux" ];
then
    echo "----- Compiling code for Linux -----"
    make linux
fi

if [ "$1" == "" ];
then
    echo "----- Default: Compiling code for Linux -----"
    make linux
fi