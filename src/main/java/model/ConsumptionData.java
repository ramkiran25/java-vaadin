package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ConsumptionData implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID")
  private Integer id;

  @Column(name = "DATE_TIME")
  private String documentDateTime;

  @Column(name = "MEASUREMENT_UNIT")
  private String measurementUnit;

  @Column(name = "MEASURMENT_PRICE")
  private String measurmentPrice;

  @Column(name = "DOCUMENT_IDENTIFICATION")
  private String documentIdentification;

  @Column(name = "ACCOUNTING_POINT")
  private String accountingPoint;

  public ConsumptionData(String documentIdentification, String documentDateTime,
      String measurementUnit, String measurmentPrice, String accountingPoint) {

    this.documentIdentification = documentIdentification;
    this.documentDateTime = documentDateTime;
    this.measurementUnit = measurementUnit;
    this.measurmentPrice = measurmentPrice;
    this.accountingPoint = accountingPoint;
  }

}
