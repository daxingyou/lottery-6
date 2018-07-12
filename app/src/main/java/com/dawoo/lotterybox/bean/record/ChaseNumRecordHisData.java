package com.dawoo.lotterybox.bean.record;

import com.dawoo.lotterybox.net.BaseHttpResult;

import java.util.List;

/**
 * Created by benson on 18-2-18.
 */

public class ChaseNumRecordHisData extends BaseHttpResult {

    private List<ChaseNumRecordHis> data;
    private Count extend;

    public Count getExtend() {
        return extend;
    }

    public void setExtend(Count extend) {
        this.extend = extend;
    }


    public List<ChaseNumRecordHis> getData() {
        return data;
    }

    public void setData(List<ChaseNumRecordHis> data) {
        this.data = data;
    }


    public static class Count {
        private int totalCount;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }

}
