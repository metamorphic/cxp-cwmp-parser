package model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by markmo on 9/04/2016.
 */
@XmlRootElement(name = "GetOrdersHistoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOrdersHistoryResponse {

    private List<OrderStatus> orderStatuses;

    public List<OrderStatus> getOrderStatuses() {
        return orderStatuses;
    }

    public void setOrderStatuses(List<OrderStatus> orderStatuses) {
        this.orderStatuses = orderStatuses;
    }
}
