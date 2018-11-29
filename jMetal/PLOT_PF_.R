#setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/experiments/VehicleRoutingStudy/referenceFronts")
setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/results")
a <- read.table("VRP_.pf")
plot(a,lwd=1, xlab="Distancia", pch=17, col="red", ylab="Penalización ambiental", las=1, panel.first=grid(), 
     main="Resultados para grafo conexo de dimensión 100")

setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/results")
b <- read.table("FUN_MOCell_.tsv")
points(b, col="blue", lwd=2, pch=1, add =TRUE)


c <- read.table("FUN_NSGAII_.tsv")
points(c, col="green", pch=1, add =TRUE)

legend(140, 90, legend=c("Frente artificial de Pareto", "MOCell", "NSGAII"),
       col=c("red", "blue", "green"), pch = c(17,1,1), cex=0.9)

abline(v=94, col=c("red"), lty=c(3), lwd=c(2))
abline(h=49, col=c("red"), lty=c(3), lwd=c(2))