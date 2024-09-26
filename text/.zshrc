export LSCOLORS=cxfxcxdxbxegedabagacad
alias ll='ls -alFG'
alias la='ls -AG'
alias l='ls -CFG'
alias lll='ls -alhFG'
alias ls='ls -CFG'
alias ls -l='ls -lG'
alias ls -a='ls -aG'
alias ls -h='ls -hG'
alias ls -la='ls -laG'
alias ls -lah='ls -lahG'
alias cdh='cd ~'
alias tree='tree -C'
source ~/.zsh/git-prompt.sh

GIT_PS1_SHOWDIRTYSTATE=true
GIT_PS1_SHOWUNTRACKEDFILES=true
GIT_PS1_SHOWSTASHSTATE=true
GIT_PS1_SHOWUPSTREAM=auto
# PROMPTの修正
setopt PROMPT_SUBST ; PS1='[%F{red}UN:%n%f@%F{yellow}HN:%m%f] %F{green}CD:[%~]%f %F{cyan}<DateTime:%D{%Y/%m/%d %H:%M:%S}>%f %F{magenta}$(__git_ps1 "HEAD->[%s]")%f
%F{blue}->%f %# '