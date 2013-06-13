#!/bin/bash
NAME="UglyLang build"
RES=$( guake -S "$NAME" )
if [ "$RES" == "0" ]; then
    guake -n "$( pwd )"
    guake -r "$NAME"
fi
guake -e "ant | tee make.log && ctags -R src && gvim --servername $1 --remote-send \":cg<CR>\" && guake -t"
guake -t
