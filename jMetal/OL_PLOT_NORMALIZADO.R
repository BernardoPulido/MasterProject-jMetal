setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/experiments/VehicleRoutingStudy/referenceFronts")
#setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/results")
a <- read.table("VRP.pf")

setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/results")
b <- read.table("inicialMOCELL.tsv")
b_filtrado = subset(b, V1 != Inf)

normalize <- function(x) {
  return ((x - min(a)) / (max(b_filtrado) - min(a)))
}
todos =rbind(a, b_filtrado)

#Normalización Min-Max
a_norm <- as.data.frame(lapply(a, normalize))
# One could also use sequence such as df[1:2]
#a_norm <- as.data.frame(lapply(a[1:2], normalize))
# Note df[2]
#a_norm <- as.data.frame(lapply(a[2], normalize))
# Note df["Salary"]
#a_norm <- as.data.frame(lapply(a["Salary"], normalize))

#Z-Score
#a_norm <- as.data.frame( scale(a[1:2] ))

b_filtrado = subset(b, V1 != Inf)
b_norm <- as.data.frame(lapply(b_filtrado, normalize))
#b_norm <- as.data.frame( scale(b_filtrado[1:2] ))

plot(b_norm,lwd=2, xlab="Distancia", pch=1, col="brown2", ylab="Penalización ambiental", las=1, panel.first=grid())

points(a_norm, col="darkcyan", lwd=1, pch=17, add =TRUE)
