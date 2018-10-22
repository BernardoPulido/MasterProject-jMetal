write("", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex",append=FALSE)
resultDirectory<-"experiments/VehicleRoutingStudy/data"
latexHeader <- function() {
  write("\\documentclass{article}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\title{StandardStudy}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\usepackage{amssymb}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\author{A.J.Nebro}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{document}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\maketitle", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\section{Tables}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
}

latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\caption{", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write(problem, "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write(".HV.}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)

  write("\\label{Table:", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write(problem, "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write(".HV.}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)

  write("\\centering", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{scriptsize}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{tabular}{", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write(tabularString, "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write(latexTableFirstLine, "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\hline ", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
}

printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { 
  file1<-paste(resultDirectory, algorithm1, sep="/")
  file1<-paste(file1, problem, sep="/")
  file1<-paste(file1, indicator, sep="/")
  data1<-scan(file1)
  file2<-paste(resultDirectory, algorithm2, sep="/")
  file2<-paste(file2, problem, sep="/")
  file2<-paste(file2, indicator, sep="/")
  data2<-scan(file2)
  if (i == j) {
    write("--", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  }
  else if (i < j) {
    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) >= median(data2)) {
        write("$\\blacktriangle$", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
}
      else {
        write("$\\triangledown$", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
}
}
    else {
      write("$-$", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
}
  }
  else {
    write(" ", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  }
}

latexTableTail <- function() { 
  write("\\hline", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{tabular}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{scriptsize}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{table}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
}

latexTail <- function() { 
  write("\\end{document}", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
}

### START OF SCRIPT 
# Constants
problemList <-c("VRP") 
algorithmList <-c("MOCell", "NSGAII") 
tabularString <-c("lc") 
latexTableFirstLine <-c("\\hline  & NSGAII\\\\ ") 
indicator<-"HV"

 # Step 1.  Writes the latex header
latexHeader()
tabularString <-c("| l | p{0.15cm } | ") 

latexTableFirstLine <-c("\\hline \\multicolumn{1}{|c|}{} & \\multicolumn{1}{c|}{NSGAII} \\\\") 

# Step 3. Problem loop 
latexTableHeader("VRP ", tabularString, latexTableFirstLine)

indx = 0
for (i in algorithmList) {
  if (i != "NSGAII") {
    write(i , "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
    write(" & ", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)

    jndx = 0
    for (j in algorithmList) {
      for (problem in problemList) {
        if (jndx != 0) {
          if (i != j) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
          } 
          if (problem == "VRP") {
            if (j == "NSGAII") {
              write(" \\\\ ", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
            } 
            else {
              write(" & ", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
            }
          }
     else {
    write("&", "experiments/VehicleRoutingStudy/R/HV.Wilcoxon.tex", append=TRUE)
     }
        }
      }
      jndx = jndx + 1
}
    indx = indx + 1
  }
} # for algorithm

  latexTableTail()

#Step 3. Writes the end of latex file 
latexTail()

