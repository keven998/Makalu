package com.xuejian.client.lxp.module.my.adapter;

/**
 * Created by xuyongchen on 15/9/17.
 */

 import java.util.ArrayList;
 import java.util.List;

 import android.app.Activity;
 import android.content.Context;
 import android.graphics.Bitmap;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.View.OnClickListener;
 import android.view.ViewGroup;
 import android.widget.AbsListView;
 import android.widget.BaseAdapter;
 import android.widget.Button;
 import android.widget.CheckBox;
 import android.widget.FrameLayout;
 import android.widget.ImageView;
 import android.widget.TextView;

 import com.aizou.core.base.BaseApplication;
 import com.aizou.core.utils.LocalDisplay;
 import com.nostra13.universalimageloader.core.DisplayImageOptions;
 import com.nostra13.universalimageloader.core.ImageLoader;
 import com.nostra13.universalimageloader.core.assist.ImageSize;
 import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
 import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
 import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
 import com.xuejian.client.lxp.R;
 import com.xuejian.client.lxp.bean.CustomGalleryBean;
 import com.xuejian.client.lxp.common.utils.LocalImageHelper;
 import com.xuejian.client.lxp.module.my.CustomGalleryActivity;

public class GalleryAdapter extends BaseAdapter {

    private LayoutInflater infalter;
    private ArrayList<LocalImageHelper.LocalFile> data = new ArrayList<LocalImageHelper.LocalFile>();
    ImageLoader imageLoader;
    private TextView btnGalleryOk;
    private DisplayImageOptions options;
    private CustomGalleryActivity context;
    public GalleryAdapter(CustomGalleryActivity c, ImageLoader imageLoader, TextView btnGalleryOk) {
        infalter = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = imageLoader;
        this.btnGalleryOk=btnGalleryOk;
        this.context=c;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .showImageForEmptyUri(R.drawable.pic_loadfail)
                .showImageOnFail(R.drawable.pic_loadfail)
                .showImageOnLoading(R.drawable.pic_loadfail)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .setImageSize(new ImageSize(LocalDisplay.SCREEN_WIDTH_PIXELS/4, 0))
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public LocalImageHelper.LocalFile getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void selectAll(boolean selection) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).isSeleted = selection;

        }
        notifyDataSetChanged();
    }

    public boolean isAllSelected() {
        boolean isAllSelected = true;

        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).isSeleted) {
                isAllSelected = false;
                break;
            }
        }

        return isAllSelected;
    }

    public boolean isAnySelected(){
        boolean isAnySelected = false;

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSeleted) {
                isAnySelected = true;
                break;
            }
        }

        return isAnySelected;
    }

    public ArrayList<LocalImageHelper.LocalFile> getSelected() {
        ArrayList<LocalImageHelper.LocalFile> dataT = new ArrayList<LocalImageHelper.LocalFile>();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSeleted) {
                dataT.add(data.get(i));
            }
        }

        return dataT;
    }

    public void addAll(List<LocalImageHelper.LocalFile> files) {

        try {
            this.data.clear();
            this.data.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void changeSelection(View v, int position) {

        if (data.get(position).isSeleted) {
            data.get(position).isSeleted = false;
        } else {
            data.get(position).isSeleted = true;
        }
        this.notifyDataSetChanged();
    }

    List<Integer> lstPosition = new ArrayList<Integer>();
    List<View> lstView = new ArrayList<View>();
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder= null;
        if(convertView==null || convertView.getTag()==null){
            viewHolder= new ViewHolder();
            convertView = infalter.inflate(R.layout.gallery_item, null);
            viewHolder.gallery_frame = (FrameLayout) convertView.findViewById(R.id.gallery_frame);
            viewHolder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);
            viewHolder.imgQueueMultiSelected = (CheckBox) convertView.findViewById(R.id.imgQueueMultiSelected);
            convertView.setTag(viewHolder);
        }else {
            viewHolder =(ViewHolder)convertView.getTag();
        }

        int width = (LocalDisplay.SCREEN_WIDTH_PIXELS - (5*LocalDisplay.dp2px(4))) / 4;
        AbsListView.LayoutParams lytp = new AbsListView.LayoutParams(width,width);
        viewHolder.gallery_frame.setLayoutParams(lytp);
        viewHolder.imgQueue.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage(data.get(position).getThumbnailUri(), new ImageViewAware(viewHolder.imgQueue), options,
                null, null, data.get(position).getOrientation());
        viewHolder.imgQueueMultiSelected.setOnCheckedChangeListener(context);
        viewHolder.imgQueueMultiSelected.setTag(data.get(position));
        viewHolder.imgQueueMultiSelected.setChecked(LocalImageHelper.getInstance().getCheckedItems().contains(data.get(position)));
        return convertView;
    }

    public class ViewHolder {
        ImageView imgQueue;
        CheckBox imgQueueMultiSelected;
        FrameLayout gallery_frame;
    }

//	public void clearCache() {
//		imageLoader.clearDiscCache();
//		imageLoader.clearMemoryCache();
//	}

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}
