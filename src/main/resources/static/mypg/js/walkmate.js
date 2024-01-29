import * as list from "./module/list.js";
import * as page from "./module/page.js";
import {listSearchDto} from "./module/list.js";


// 모집현황 데이터 불러오기
function searchRecruitmentForm(){

    let state = $("input[name='state']:checked").val();
    console.log(state +"searchRecruitmentForm() ")
    return{
        state : state

    }

}

function moveState(state){
    let userId = $('.walkmate').data('userid');
    searchRecruitmentForm().state = state;
    console.log( searchRecruitmentForm().state + " moveState");
    list.listSearchDto(0,userId,searchRecruitmentForm(),'mypgs','myregisterwalkmatewrite',showList);
}

function showList(result){
    let text = '';
    let textInput = $('.walk-list');

    if(result.content.length === 0){
        text = `
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
                <div class="list-content " data-num ="${r.id}">
                    <div class="content-text-box">
                        <div class="list-content-title">
                            <div>${r.walkingMateTitle}</div>
                `;
            if(r.walkingMateState ==0){
                text+=` 
                            <div class="recruit-btn-box" data-walkingmateid="${r.id}">
                                <div>모집 대기</div>
                            </div>
                        `;
            }else if(r.walkingMateState == 1){
                text +=`
                            <div class="recruit-box" >
                                 <span>모집 완료</span>
                            </div>
                          `;
            }

            text += `            
                    </div>

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
                            <span class="member" >모집인원</span>
                            <span class="recruit-count">${r.walkingMateToday}/${r.walkingMatePerson}</span>
                     
                        </div>
                    </div>
                `;

            if(r.walkMateStateDtoList.length != 0) {

                text += `
                    <div class="application-user">
                        <table class="application-user-area">
                            <tr class="title-area">
                                <th>신청자</th>
                                <th>주소</th>
                                <th>승인여부</th>
                            </tr>
                  `;
                r.walkMateStateDtoList.forEach(e => {

                    text += `
                            <tr class="user-li" data-id="${e.userId}">
                          `;
                    if (e.userNickName == null) {
                        text += `<td class="userAccount">${e.userAccount}</td>`;
                    } else if (e.userNickName != null) {
                        text += `<td class="userAccount">${e.userNickName}</td>`;
                    }
                    text += `  
                                    <td class="applicationUser-city">${e.address}</td>
                                    <td class="permit-area">
                                `;
                    if (e.state == 0) {
                        if(r.walkingMateState == 1){

                            text += `    

                                        <div class="y-y" style="display: none;">
                                            <div class="y">
                                                <span class="y-click" data-number="${e.id}" >승인</span>
                                            </div>
                                        </div>
                                        
                                         <div class="n" style="display:none;">
                                            <div class="n-area">
                                                <div class="y-a">
                                                    <span class="succes">완료</span>
                                                </div>
                                            
                                                <div class="cencel-area" >
                                                    <span class="cencel">취소</span>
                                                </div>
                                            </div>
                                        </div>
                                    
                                    </td>    
                                </tr>
                                    `;}else{
                            text += `    

                                        <div class="y-y">
                                            <div class="y">
                                                <span class="y-click" data-number="${e.id}" >승인</span>
                                            </div>
                                        </div>
                                        
                                         <div class="n" style="display:none;">
                                            <div class="n-area">
                                                <div class="y-a">
                                                    <span class="succes">완료</span>
                                                </div>
                                            
                                                <div class="cencel-area" >
                                                    <span class="cencel">취소</span>
                                                </div>
                                            </div>
                                        </div>
                                    
                                    </td>    
                                </tr>`;
                        }



                    } else if (e.state == 1) {


                        if(r.walkingMateState == 1){
                            text += `
                                         <div class="y-y" style="display:none;">
                                            <div class="y">
                                                <span class="y-click">승인</span>
                                            </div>
                                        </div>
                                        
                                        
                                        <div class="n">
                                            <div class="n-area">
                                                <div class="y-a">
                                                    <span class="succes">완료</span>
                                                </div>
                                            
                                                <div class="cencel-area" style="display: none;" >
                                                    <span class="cencel" data-numbers="${e.id}">취소</span>
                                                </div>
                                            </div>
                                        </div>
                                        </td>
                                    </tr>      
                                       `;
                        }else {

                            text += `
                                         <div class="y-y" style="display:none;">
                                            <div class="y">
                                                <span class="y-click">승인</span>
                                            </div>
                                        </div>
                                        
                                        
                                        <div class="n">
                                            <div class="n-area">
                                                <div class="y-a">
                                                    <span class="succes">완료</span>
                                                </div>
                                            
                                                <div class="cencel-area" >
                                                    <span class="cencel" data-numbers="${e.id}">취소</span>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                               </tr>      
                               `;

                        }}

                });

                text += `</table>
                            </div>
                            `;
            }else if(r.walkMateStateDtoList.length == 0){
                console.log(r.walkMateStateDtoList.length)
                // $('.application-user').css('display','none');
                text += `
                               <div class="application-user" style="display: none;">
                               
                               
                               
                               </div> 
                            <div class="none-application-area">
                                    <div class="no-img-area">
                                        <div class="no-img-area-text">
                                            <img class="no-img" src="/mypg/img/wating.png"/> 
                                        </div> 
                                    </div>
                                      <div class="non-application-text">사랑스러운 매이트를 기다리는 중입니다.</div>
                                
                            </div>
                        
                        `;

                console.log("처리됫다")

            }
            text += `
                        </div>
                    </div>    
                    `;

        });
    }



    console.log(text);
    textInput.html(text);

    let paginations = $('.pagination-ul');
    page.pagination(result, paginations)
    paginations.find('a').on('click', function (e) {
        e.preventDefault();
        const page = parseInt($(this).data('page'));
        let userId = $('.walkmate').data('userid');
        searchRecruitmentForm.state=$("input[name='state']:checked").val()
        list.listSearchDto(page,userId, searchRecruitmentForm(),'mypgs','myregisterwalkmatewrite', showList)



    });

}


$(document).ready(function (){
    let userId = $('.walkmate').data('userid');
    console.log(userId);
    list.listSearchDto(0,userId,searchRecruitmentForm(),'mypgs','myregisterwalkmatewrite',showList);


    $("input[name='state']").on('change', function (){

        let status = $(this).val();
        moveState(status);
    });



})



$(document).on('click', '.y-click', function () {

    let walkingMateToday = parseInt($(this).closest('.list-content').find('.recruit-count').text().split('/')[0]);
    let walkingMatePerson = parseInt($(this).closest('.list-content').find('.recruit-count').text().split('/')[1]);

    console.log(walkingMateToday);
    console.log(walkingMatePerson);


    let walkmatestateId = $(this).data('number');
    let applicationusreId = $('.user-li').data('id');
    let userId = $('.walkmate').data('userid');
    console.log(walkmatestateId);
    console.log(applicationusreId);

    let clickedElement = $(this);

    if(walkingMateToday == walkingMatePerson){
        alert("더 이상 모집할수 없습니다.")
    }else {
        $.ajax({
            url: `/mypgs/walkmatestateupdate/${walkmatestateId}`,
            type: 'patch',
            success: function (result) {
                console.log(result);
                console.log(typeof result, result);
                if (result == 1) {

                    window.location.href = userId;
                }
            },
            error: function (a, b, c) {
                console.error(c);
            }
        });
    }
});

$(document).on('click', '.cencel', function () {
    let walkmatestateId = $(this).data('numbers');
    let applicationusreId = $('.user-li').data('id');
    let userId = $('.walkmate').data('userid');
    console.log(walkmatestateId);
    console.log(applicationusreId);


    $.ajax({
        url: `/mypgs/walkmatestatedown/${walkmatestateId}`,
        type: 'patch',
        success: function (result) {
            console.log(result);
            console.log(typeof result, result);
            if(result == 0){

                window.location.href=userId;
            }
        },
        error: function (a, b, c) {
            console.error(c);
        }
    });
});

$(document).on('click','.recruit-btn-box',function (){

    let walkingmateId = $(this).data('walkingmateid');
    console.log(walkingmateId);
    let userId = $('.walkmate').data('userid');
    $.ajax({
        url: `/mypgs/walkingmatestateupdate/${walkingmateId}`,
        type: 'patch',
        success: function (result) {
            console.log(result);
            console.log(typeof result, result);
            if(result == 1){

                window.location.href=userId;
            }
        },
        error: function (a, b, c) {
            console.error(c);
        }
    });
});


// 게시판 클릭시 상세페이지로 이동
$(document).on('click','.list-content',function (){

    let walkingmateId = $(this).data('num');
    console.log(walkingmateId);
    let userId = $('.walkmate').data('userid');

    window.location.href = `/walk/detail/${walkingmateId}/${userId}`
});