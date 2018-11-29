setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/results")
a <- read.table("FUN_MOCell.tsv")
plot(a,lwd=2,xlab="Distancia", ylab="Penalización ambiental", las=1, panel.first=grid())
abline(v=33, col=c("red"), lty=c(3), lwd=c(2))
abline(h=7, col=c("red"), lty=c(3), lwd=c(2))

legend(42.5,12.5, legend = c("MOCell", "Soluciones óptimas"), 
       col = c("black", "red"),
       lty = c(0, 3), lwd = c(2, 1),
       pch = c(1, NA), cex=0.9)