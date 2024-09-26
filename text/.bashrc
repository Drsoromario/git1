if [ "$EUID" -eq 0 ]; then
    # rootユーザーの場合
    export PS1='[\[\e[31m\]\u\[\e[m\]@\[\e[33m\]\h\[\e[m\]] CD:[\[\e[32m\]\w\[\e[m\]] \[\e[34m\]<DateTime:\D{%Y/%m/%d %H:%M:%S}>\[\e[m\]
\[\e[36m\]->\[\e[m\]# '
else
    # 一般ユーザーの場合
    export PS1='[\[\e[31m\]\u\[\e[m\]@\[\e[33m\]\h\[\e[m\]] CD:[\[\e[32m\]\w\[\e[m\]] \[\e[34m\]<DateTime:\D{%Y/%m/%d %H:%M:%S}>\[\e[m\]
\[\e[36m\]->\[\e[m\]$ '
fi
