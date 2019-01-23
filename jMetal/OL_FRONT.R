setwd("C:/Users/luisb/Documents/GitHub/MasterProject-jMetal/jMetal/experiments/VehicleRoutingStudy/referenceFronts")
a <- read.table("VRP.pf")
plot(a,lwd=1, xlab="Distancia", pch=17, col="red", ylab="Penalización ambiental", las=1, panel.first=grid())