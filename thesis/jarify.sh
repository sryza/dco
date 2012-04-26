#!/bin/bash

#jar -cvf tspls.jar -C bin/ pls/
cd bin
#jar -cvf tspls.jar pls/PlsMetadata.class pls/PlsSolution.class pls/PlsMaster.class pls/PlsMaster$StatsThread.class pls/PlsUtil.class pls/SolutionData.class pls/WritableSolution.class pls/SolutionIdGenerator.class pls/LnsSolutionData.class pls/vrp/ pls/stats/ pls/reduce/ pls/map/
jar -cvf tspls.jar pls/*.class pls/vrp/ pls/stats/ pls/reduce/ pls/map/

mv tspls.jar ../tspls.jar
cd ..