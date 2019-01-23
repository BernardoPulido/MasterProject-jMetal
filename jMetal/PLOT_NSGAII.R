setwd("C:/Users/luisb/Documents/GitHub/MasterProject-jMetal/jMetal/results")
a <- read.table("FUN_NSGAII.tsv")
plot(a,lwd=2,xlab="Distancia", ylab="Penalización ambiental", las=1, panel.first=grid())
abline(v=33, col=c("red"), lty=c(3), lwd=c(2))
abline(h=7, col=c("red"), lty=c(3), lwd=c(2))

c <- read.table("FUN_MOCell.tsv")
points(c, col="orange", pch=1, add =TRUE)