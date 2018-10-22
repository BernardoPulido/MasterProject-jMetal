setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/experiments/VehicleRoutingStudy/referenceFronts")
a <- read.table("VRP.pf")
plot(a,lwd=1, xlab="Distancia", pch=17, col="red", ylab="Penalización ambiental", las=1, panel.first=grid())

setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/results")
b <- read.table("FUN_MOCell.tsv")
points(b, col="blue", lwd=2, pch=1, add =TRUE)


c <- read.table("FUN_NSGAII.tsv")
points(c, col="green", pch=1, add =TRUE)

legend(140, 90, legend=c("Frente aproximado de Pareto", "MOCell", "NSGAII"),
       col=c("red", "blue", "green"), pch = c(17,1,1), cex=0.9)