import * as list from "./module/list.js";
import * as page from "./module/page.js";
import {listSearchDto} from "./module/list.js";


$(document).ready(function (){
    let userId = $('.mateapplicationmain').data('userid');
    list.listSearchDto(0,userId,searchRecruitmentForm(),'mypgs','applicationwalkmate',showList);


    $("input[name='state']").on('change', function (){

        let status = $(this).val();
        moveState(status);
    });
})






function searchRecruitmentForm(){

    let state = $("input[name='state']:checked").val();
    return{
        state : state

    }

}

function moveState(state){
    let userId = $('.mateapplicationmain').data('userid'); // 데이터 불러오기위한 클래스 확인
    searchRecruitmentForm().state = state;
    list.listSearchDto(0,userId,searchRecruitmentForm(),'mypgs','applicationwalkmate',showList);
}


function showList(result){
    let text ='';
    let textInput = $('.walkapplication-list');

    if(result.content.length === 0){
        text =`
            
                <div class="nonepage">
                    <div class="none-img-area">
                        <div class="none-img">
                            <img src="../img/b556fdf429d8de25c3acf62f8186ddb9.png"/>
                        </div>
                    </div>
                    <div class="nonetext-area">
                        <p>등록된 내용이 없습니다.</p>
                    </div>
                </div>
              
            `;
    }else {
        result.content.forEach(r => {
             text += `
              
                  <div class="list-content">
                            <div class="content-text-box">
                                <div class="list-content-title">${r.walkingMateTitle}</div>
                                <div class="list-content-content">
                                    <div class="list-content-content-detail">
                                        ${r.walkingMateContent}
                                    </div> 
                                </div>
                                <div class="list-content-etc">
                                    <div class="list-content-address">
                                        <span>${r.walkCity} ${r.walkCounty}</span>
                                    </div>
                                
                                    <div class="list-content-recruit">
                                        <span>모집인원</span>
                                        <span class="recruit-count">${r.walkingMateToday}/${r.walkingMatePerson}</span>
                                    </div>
                                    `;
                                    if(r.walkingMateState ==0) {

                                        text +=`
                                            <div class="recruit-btn-box">
                                                <span>모집 중</span>
                                            </div>
                                            `;

                                    }else if(r.walkingMateState ==1){
                                        text +=`    
                                            <div class="recruit-btn-box">
                                                <span>모집 완료</span>
                                            </div>
                                            `;
                                    }
                                text +=`    
                                </div>
                            </div>
                            <div class="content-img-box">
                                <div class="content-img">
                                    <img src="/mypgs/petImg?fileFullPath=${r.petPath + '/' + r.petUuid + '_' + r.petFileName}">
                                </div>
                            </div>
                          
                  </div>
                       
             `;
        })

    }

    textInput.html(text);

    let paginations = $('.pagination-ul');
    page.pagination(result, paginations)
    paginations.find('a').on('click', function (e) {
        e.preventDefault();
        const page = parseInt($(this).data('page'));
        let userId = $('.mateapplicationmain').data('userid');
        searchRecruitmentForm.state=$("input[name='state']:checked").val()
        list.listSearchDto(page,userId, searchRecruitmentForm(),'mypgs','applicationwalkmate', showList)



    });


}