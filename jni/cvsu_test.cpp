#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <android/log.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include "cvsu_config.h"
#include "cvsu_macros.h"
#include "cvsu_types.h"
#include "cvsu_memory.h"
#include "cvsu_quad_forest.h"

string DrawTrees_name = "DrawTrees";

using namespace cv;

extern "C" {

JNIEXPORT void JNICALL
Java_info_amnipar_cvsu_CVSUActivity_ProcessGray(JNIEnv*, jobject, jlong addrGray)
{
  unsigned int row, col;
  uchar *value;
  Mat& mGr  = *(Mat*)addrGray;
  __android_log_print(ANDROID_LOG_INFO,"libcvsu","ProcessGray: %d %d %d %d",mGr.cols,mGr.rows,mGr.step[0],mGr.step[1]);
  for (row = 1; row < mGr.rows; row += 2) {
    for (col = 0; col < mGr.cols; col++) {
      value = mGr.data + row * mGr.step[0] + col * mGr.step[1];
      *value = 0;
    }
  }
}

JNIEXPORT void JNICALL
Java_info_amnipar_cvsu_CVSUActivity_ProcessRgba(JNIEnv*, jobject, jlong addrRgba)
{
  unsigned int row, col;
  uchar *value;
  Mat *mRgb = (Mat*)addrRgba;
  __android_log_print(ANDROID_LOG_INFO,"libcvsu","ProcessRgba: %d, %d",mRgb->cols, mRgb->rows);
  for (row = 1; row < mRgb->rows; row += 2) {
    for (col = 0; col < mRgb->cols; col++) {
      value = mRgb->data + row * mRgb->step[0] + col * mRgb->step[1];
      *value = 0;
      value++;
      *value = 255;
      value++;
      *value = 255;
    }
  }
}

JNIEXPORT void JNICALL
Java_info_amnipar_cvsu_CVSUActivity_DrawTrees(JNIEnv*, jobject, jlong addrGray, jlong addrRgba)
{
  TRY();
  quad_forest forest;
  pixel_image rgb_image;
  Mat *mGr  = (Mat*)addrGray;
  Mat *mRgb = (Mat*)addrRgba;
  __android_log_print(ANDROID_LOG_INFO,"libcvsu","DrawTrees");
  CHECK(quad_forest_create_from_data(&forest, mGr->data, mGr->cols, mGr->rows, mGr->step[0], mGr->step[1], 8, 4));
  /*__android_log_print(ANDROID_LOG_INFO,"libcvsu","created forest");*/
  CHECK(pixel_image_create_from_data(&rgb_image, mRgb->data, p_U8, RGB, mRgb->cols, mRgb->rows, mRgb->step[1], mRgb->step[0]));
  /*__android_log_print(ANDROID_LOG_INFO,"libcvsu","created target image");*/
  CHECK(quad_forest_segment_with_boundaries(&forest));
  /*CHECK(quad_forest_segment_with_overlap(&forest, 2.5, 0.6, 0.5));*/
  CHECK(quad_forest_draw_trees(&forest, &rgb_image, TRUE));

  FINALLY(DrawTrees);
  quad_forest_destroy(&forest);
  memory_deallocate((data_pointer*)&rgb_image.rows);
  pixel_image_nullify(&rgb_image);
}

}
