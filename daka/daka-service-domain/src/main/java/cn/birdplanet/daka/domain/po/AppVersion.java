package cn.birdplanet.daka.domain.po;

import cn.birdplanet.toolkit.extra.support.LocalDateTimeDeserializer;
import cn.birdplanet.toolkit.extra.support.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
@Table(name = "t_app_version")
public class AppVersion {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "os")
  private String os;

  @Column(name = "app_id")
  private String appId;


  @Column(name = "title")
  private String title;

  @Column(name = "build_name")
  private String buildName;

  @Column(name = "build_number")
  private String buildNumber;

  @Column(name = "contents")
  private String contents;

  @Column(name = "is_force")
  private Integer isForce;

  @Column(name = "apk_download_url")
  private String apkDownloadUrl;
  @Column(name = "apk_download_url_2")
  private String apkDownloadUrl2;

  @Column(name = "status")
  private String status;

  @Column(name = "created_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;
  @Column(name = "updated_at")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;

  public AppVersion(String buildName) {
    this.buildName = buildName;
  }
}
