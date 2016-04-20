package com.laohudada.yingxin.http.mdel;

/**
 * Created by tao on 2016/1/9.
 */
public class TicketInfo {

    private int id;
    private TicketDetailInfo ticketDetail;

    public TicketDetailInfo getTicketDetail() {
        return ticketDetail;
    }

    public void setTicketDetail(TicketDetailInfo ticketDetail) {
        this.ticketDetail = ticketDetail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
