setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/experiments/VehicleRoutingStudy/referenceFronts")
#setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/results")
a <- read.table("VRP.pf")

setwd("C:/Users/luis_/Documents/GitHub/MasterProject-jMetal/jMetal/results")
b <- read.table("inicialMOCELL.tsv")
b_filtrado = subset(b, V1 != Inf)

final_NSGAII <- read.table("finalNSGAII.tsv")
final_NSGAII_filtrado = subset(final_NSGAII, V1 != Inf)

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

b_norm <- as.data.frame(lapply(b_filtrado, normalize))

final_NSGAII_normalizado <- as.data.frame(lapply(final_NSGAII_filtrado, normalize))
#b_norm <- as.data.frame( scale(b_filtrado[1:2] ))

plot(b_norm,lwd=2, xlab="Distancia", pch=1, col="brown2", ylab="Penalización ambiental", las=1, panel.first=grid())

points(final_NSGAII_normalizado, col="darkcyan", lwd=1, pch=17, add =TRUE)


legend(0, 0.08, legend = c("Población inicial", "Última población"), 
       col = c("brown2", "darkcyan"),
       lty = c(0, 0), lwd = c(2, 2),
       pch = c(1, 17), cex=0.9)

