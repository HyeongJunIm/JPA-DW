import * as list from './module/list.js';
import * as page from './module/page.js';


$(document).ready(function (){
    let userId = $('.myfreeboardmain').data('userid');
    list.list(0,userId,'mypgs','myfreeBoardLists',showUserFreeboardList);

// 리스트 클리시 세부 페이지 이동
    $('.list-content').on('click',function (){
        let id = $(this).data('id');
        window.location.href = "/community/freeBoardDetail/"+id;
    })

})


function showUserFreeboardList(result) {
    let text = '';
    let textInput = $('.list-contents-box');


    console.log(result.length);
    if (result.content.length === 0) {
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

        $('.nonepage').css('display','block');
    }else{
        result.content.forEach(r => {

            text += ` <div class="list-content" th:data-id="${r.id}">
                        <div class="content-text-box">
                            <div class="list-content-title">${r.freeBoardTitle}</div>
                            <div class="list-content-content">
                                <div class="list-content-content-detail">
                                    ${r.freeBoardContent}
                                </div> 
                            </div>
                        `;

            text +=`<div class="list-content-etc">
                                <div class="list-content-id">
                            `;
            if(r.userFileDtos.userFileId ==null){
                text += `
                                <div class="list-content-id-img"><img src="/mypg/img/b556fdf429d8de25c3acf62f8186ddb9.png" alt=""></div>
                                    `;

            }else if(r.userFileDtos.userFileId !=null){

                text += `
                                <div class="list-content-id-img"><img src="/mypgs/userImg?fileFullPath=${r.userFileDtos.route + '/' + r.userFileDtos.uuid + '_' + r.userFileDtos.name}"  alt=""></div>
                                                `;

            }

            if(r.userNickName == null){
                text +=`
                                     <span>${r.userAccount}</span>
                                    `;

            } else if(r.userNickName != null){
                text += `
                                    <span>${r.userNickName}</span>
                                    `;

            }
            text +=`
                                </div>
                                <div class="list-content-reply">
                                    <span>댓글</span>
                                    <span class="reply-count">${r.commentCount}</span>
                                </div>
                              
                            </div>
                            
                          </div>  
                            `;



            text += `
                                    
                                       <div class="content-img-box">
                                            <div class="content-img">
                                                <img src="/mypgs/freeImg?fileFullPath=${r.freeBoardImgDtos.freeBoardImgRoute + '/' + r.freeBoardImgDtos.freeBoardImgUuid + '_' + r.freeBoardImgDtos.freeBoardImgName}" alt="">
                                            </div>
                                       </div>
                                    `;



            text += `
                            </div>
                        
                        `;


        })

    }
    textInput.html(text);

    let paginations = $('.pagination-ul');
    page.pagination(result,paginations)
    paginations.find('a').on('click', function (e) {
        e.preventDefault();
        let userId = $('.myfreeboardmain').data('userid');
        const page = parseInt($(this).data('page'));
        list.list(page, userId,'mypgs','myfreeBoardLists',showUserFreeboardList);
    });


}


