package com.zb.dao.imp;

import com.alibaba.fastjson.JSON;
import com.zb.entity.DayScoreRule;
import com.zb.entity.LevelRule;
import com.zb.util.database.BaseDao;

import java.util.List;

public class ScoreRuleImp extends BaseDao {

    /**
     * 获取每天得分的规则表数据，三表关联查询
     * @return DayScoreRule实体
     */
    public List<DayScoreRule> getScoreRule() {
        String sql = "select d.*,p.platename,o.operaname from \n" +
                "dayscorerule d\n" +
                "inner join tb_plate p \n" +
                "on d.plate_id = p.id \n" +
                "inner join tb_operation o \n" +
                "on d.opera_id = o.id";
        return selectMany(sql, DayScoreRule.class);
    }

    public List<LevelRule> getLevelRule() {
        String sql = "select * from levelrule";
        return selectMany(sql, LevelRule.class);
    }

    public static void main(String[] args) {
        ScoreRuleImp scoreRuleImp = new ScoreRuleImp();
        List<LevelRule> levelRule = scoreRuleImp.getLevelRule();
        List dayScoreRule = scoreRuleImp.getScoreRule();
        System.out.println(JSON.toJSONString(levelRule));
        System.out.println(JSON.toJSONString(dayScoreRule));
    }
}
