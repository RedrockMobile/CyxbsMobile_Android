package com.mredrock.cyxbs.course.page.course.viewmodel;

import com.mredrock.cyxbs.course.page.course.data.AffairData;
import com.mredrock.cyxbs.course.page.course.data.StuLessonData;
import com.mredrock.cyxbs.course.page.course.data.StuLessonDataKt;
import com.mredrock.cyxbs.course.page.course.model.StuLessonRepository;
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity;
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel;
import com.mredrock.cyxbs.lib.course.internal.item.ISingleColumnItemData;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/27 19:41
 */
public class Test {
  void test() {
    Observable<List<? extends ISingleColumnItemData>> observable1 = StuLessonRepository.INSTANCE.observeSelfLesson()
      .map((Function<List<StuLessonEntity>, List<StuLessonData>>) stuLessonEntities ->
        StuLessonDataKt.toStuLessonData(stuLessonEntities, StuLessonData.Who.Self)
      );
  
    Observable<List<? extends ISingleColumnItemData>> observable2 = Observable.just(new ArrayList<AffairData>());
    
    List<Observable<List<? extends ISingleColumnItemData>>> list = new ArrayList<>();
    list.add(observable1);
    list.add(observable2);
    
    Observable.combineLatest(list, new Function<Object[], HomeCourseViewModel.HomePageResult>() {
      @Override
      public HomeCourseViewModel.HomePageResult apply(Object[] lists) {
        return new HomeCourseViewModel.HomePageResultImpl((List<StuLessonData>) lists[0], (List<StuLessonData>) lists[1], (List<AffairData>) lists[2]);
      }
    }).subscribe();
  }
}
