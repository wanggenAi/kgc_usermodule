package com.zb.service.imp;

import com.zb.dao.imp.ScoreRuleImp;
import com.zb.entity.DayScoreRule;
import com.zb.entity.LevelRule;
import com.zb.service.inter.ScoreRuleService;

import java.util.List;

public class ScoreRuleServiceImp implements ScoreRuleService {

    private ScoreRuleImp scoreRuleImp = new ScoreRuleImp();

    @Override
    public List<LevelRule> getLevelRule() {
        return scoreRuleImp.getLevelRule();
    }

    @Override
    public List<DayScoreRule> getDayScoreRule() {
        return scoreRuleImp.getScoreRule();
    }

     
}
