package com.zb.service.inter;

import com.zb.entity.DayScoreRule;
import com.zb.entity.LevelRule;

import java.util.List;

public interface ScoreRuleService {
    public List<LevelRule> getLevelRule();

    public List<DayScoreRule> getDayScoreRule();
}
