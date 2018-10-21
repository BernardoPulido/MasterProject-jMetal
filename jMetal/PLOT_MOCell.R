setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal")
a <- read.table("FUN_MOCell.tsv")
plot(a,lwd=2,xlab="Distancia", ylab="Penalización ambiental", las=1, panel.first=grid())
abline(v=33, col=c("red"), lty=c(3), lwd=c(2))
abline(h=7, col=c("red"), lty=c(3), lwd=c(2))

