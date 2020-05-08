package com.rocketmotordesign.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rocketmotordesign.controler.request.BasicComputationRequest;
import com.rocketmotordesign.controler.request.FinocylComputationRequest;
import com.rocketmotordesign.controler.request.StarGrainComputationRequest;

@Service
public class ConfigRestricterService {

    private final Integer finocylLimit;
    private final Integer starlLimit;
    private final Integer maxStarBranches;

    public ConfigRestricterService(
            @Value("${computation.finocyl.limit.size:400}") Integer finocylLimit,
            @Value("${computation.star.limit.size:400}") Integer starlLimit,
            @Value("${computation.star.limit.size:6}") Integer maxStarBranches) {
        this.finocylLimit = finocylLimit;
        this.starlLimit = starlLimit;
        this.maxStarBranches = maxStarBranches;
    }

    public void applyRestriction(BasicComputationRequest request) throws UnauthorizedValueException {
        if(request instanceof FinocylComputationRequest) {
            if(request.getExtraConfig().getNumberOfCalculationLine() == null){
                request.getExtraConfig().setNumberOfCalculationLine(finocylLimit);
            }
        }

        if(request instanceof StarGrainComputationRequest) {
            if(request.getExtraConfig().getNumberOfCalculationLine() == null){
                request.getExtraConfig().setNumberOfCalculationLine(starlLimit);
            }

            if(((StarGrainComputationRequest)request).getPointCount()> maxStarBranches){
                throw new UnauthorizedValueException("Due to performance issue on METEOR, you can't use more than " + maxStarBranches + " branches on star grain.");
            }
        }
    }
}
