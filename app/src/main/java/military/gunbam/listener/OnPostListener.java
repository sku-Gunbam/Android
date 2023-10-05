package military.gunbam.listener;

import military.gunbam.model.Post.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();
}
