1、spark-submit --master yarn --deploy-mode cluster  --class cn.td.etl.streaming.HuaRuiETL  --executor-memory 1G --num-executors 5 --executor-cores 2  --name huarui_entry_real  HuaRuiRealETL-1.0-SNAPSHOT.jar
2、spark-submit --master yarn --deploy-mode cluster  --class cn.td.etl.streaming.BlackETL  --executor-memory 1G --num-executors 5 --executor-cores 2  --name huarui_entry_stats HuaRuiRealETL-1.0-SNAPSHOT.jar
3、spark-submit --master yarn --deploy-mode cluster  --class cn.td.etl.streaming.EntryEtl  --executor-memory 1G --num-executors 5 --executor-cores 2  --name huarui_black HuaRuiRealETL-1.0-SNAPSHOT.jar