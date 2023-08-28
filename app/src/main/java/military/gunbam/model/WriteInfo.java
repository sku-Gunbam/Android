package military.gunbam.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class WriteInfo {
    private String id; // To store Firestore document ID
    private String title;
    private String contents;
    private String publisher;
    private int recommendationCount;
    private boolean isAnonymous;
    private Timestamp uploadTime;

    /**
     * 생성자 메서드: WriteInfo 객체를 생성하는 역할을 수행합니다.
     *
     * @param id                  Firestore 문서 ID
     * @param title               게시물의 제목
     * @param contents            게시물의 내용
     * @param publisher           게시물의 작성자 또는 출판자
     * @param recommendationCount 게시물의 추천 수
     * @param isAnonymous         익명 여부 (true면 익명, false면 비익명)
     * @param uploadTime          게시물 업로드 시간 (Timestamp 형식)
     */
    public WriteInfo(String id, String title, String contents, String publisher, int recommendationCount, boolean isAnonymous, Timestamp uploadTime){
        this.id = id; // Set the Firestore document ID
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.recommendationCount = recommendationCount;
        this.isAnonymous = isAnonymous;
        this.uploadTime = uploadTime;
    }
    public WriteInfo(String title, String contents, String publisher, int recommendationCount, boolean isAnonymous, Timestamp uploadTime){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.recommendationCount = recommendationCount;
        this.isAnonymous = isAnonymous;
        this.uploadTime = uploadTime;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getContents(){
        return this.contents;
    }
    public void setContents(String contents){
        this.contents = contents;
    }

    public String getPublisher(){
        return this.publisher;
    }
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

    public int getRecommendationCount() {
        return recommendationCount;
    }
    public void setRecommendationCount(int recommendationCount) {
        this.recommendationCount = recommendationCount;
    }

    public boolean getIsAnonymous() {
        return isAnonymous;
    }
    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    // Exclude this field from Firestore serialization
    @Exclude
    public String getId() {
        return id;
    }
    // Set the Firestore document ID
    @Exclude
    public void setId(String id) {
        this.id = id;
    }
}