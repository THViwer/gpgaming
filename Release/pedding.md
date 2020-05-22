创建默认代理

1. 新增表
    // Agent 代理表
    Commission 佣金配置表
    MemberRelation 会员关系表
    AgentReportDaily 代理日报表
    AgentApply 代理申请表
    
2. 新增、修改字段
    Member 新增 role、promoteCode、agentId
    Withdraw 新增 role
    MemberDailyReport 新增 agentId、username、superiorAgentId 
                    修改
                     backwaterExecution -> rebateExecution
                     backwaterMoney -> rebateAmount
                     artificialMoney -> artificialAmount
                     depositMoney -> depositAmount
                     thirdPayMoney -> thirdPayAmount
                     withdrawMoney -> withdrawAmount
                     withdrawMoney -> withdrawAmount
    ArtificialOrder 新增 money
    ClientPlatformDailyReport 新增 rebateAmount、commissionAmount、bossId 
                              修改 backwaterAmount -> rebateAmount
                              删除：thirdPaySequence、depositSequence、
                              
    WalletEvent 修改：Backwater -> Rebate
    update wallet_note set `event` = 'Rebate' where `event` = 'Backwater';
    TransferOrder  新增 promotion_pre_money
    
2. 添加默认代理
    



admin修改 
1. 查询报表修改字段：/report/member
            查询：
                minBackwaterMoney -> minRebateAmount
                minPromotionMoney -> minPromotionAmount
            返回：
                withdrawMoney -> withdrawAmount
                artificialMoney -> artificialAmount
                depositMoney -> depositAmount
                thirdPayMoney -> thirdPayAmount
                backwaterMoney -> rebateAmount
                promotionMoney -> promotionAmount
                
   会员报表修改字段： /report/member
                s
                


