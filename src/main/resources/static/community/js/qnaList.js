import * as list from './module/list.js'
import  * as page from './module/page.js';
import * as date from './module/form.js'
import {listSearchDto} from "./module/list.js";



// 페이지에 리스트 뿌려주는 스크립트

$(document).ready(function (){
    console.log("들어왔다")
    // 초기화면
    list.listSearchDto(0,searchQnaBoardForm(),'qnar','qnalist',showQnaBoardList);

    page.enterKey('.search-question','.search-btn');


    // 라디오 버튼 변경시 마다 동적으로 변경
    $("input[name='state']").on('change', function (){

        let cate = $(this).val();
        updateStatus(cate);

    })




})

// 검색버튼
$('.sbtn').on('click',function (){
    list.listSearchDto(0,searchQnaBoardForm(),'qnar','qnalist',showQnaBoardList);

})

function updateStatus(cate){
    searchQnaBoardForm().cate = cate;
    console.log(searchQnaBoardForm().cate);
    list.listSearchDto(0,searchQnaBoardForm(),'qnar','qnalist',showQnaBoardList);

}



//input에서 검색한 내용 넘겨주기
function searchQnaBoardForm(){
    let cate = $("input[name='state']:checked").val();
    let keyword = $('.search-question').val();
    console.log(keyword);
    console.log(cate)
    return {
        keyword : keyword,
        cate : cate
    };
}

function showQnaBoardList(result) {
    let text = '';
    let textInput = $('.list-contents-box');
    console.log("list")

    result.content.forEach(r => {
        let nowTime = new Date();
        let rdTime = new Date(r.questionRd);
        let time = nowTime-rdTime;
        let timedi = Math.floor(time/(1000*60));
        let timenow = "";
        if(timedi <60){
            timenow = timedi +"분 전"
        }else{
            let ttdi = Math.floor(timedi/ 60);
            if(ttdi < 24){
                timenow = ttdi +"시간 전"
            }else {
                let ddi = Math.floor(ttdi / 24);
                timenow = ddi + "일 전";
            }
        }

        text += `
                <a href="/qna/qnaDetail/${r.id}" class="qnadetailbtn">
                        <div class="list-content">
                            <div class="list-content-title">${r.questionTitle}
                            </div>
                            <div class="list-content-content">
                                    <div class="list-content-content-detail">
                                        ${r.questionContent}
                                    </div>        
                            </div>
                            <div class="list-content-etc">
                                <div class="list-content-id">
                        `;


        if(r.userFileId == 0){
            text +=`   <div class="list-content-id-img">
                                         <img src= "/img/dogImg.jpg" alt="">
                                            </div>
                                     `;
        }else if(r.userFileId != 0){

            text +=`   <div class="list-content-id-img">
                                                <img src= "/qnar/qnaUserImg?userImgPath=${r.route + '/' + r.uuid + '_' + r.name}" alt="">
                                            </div>
                                    `;

        }




        if(r.userNickName ==null){
            text+=`
                                    <span class="userName">${r.userAccount}</span>
                                `;

        }else if(r.userNickName != null){
            text+=`
                                    <span class="userName">${r.userNickName}</span>
                                `;
        }

        text+=`
                                </div>
                                <div class="list-content-reply">
                                    <span>댓글</span>
                                    `;
        if(r.commentCount == null ){
            text += `
                                    <span class="reply-count">0</span>
                                    `;}else{
            text += `
                                    <span class="reply-count">${r.commentCount}</span>
                                    `;
        }

        text += `      
                                </div>
                                <div class="list-content-time">
                                    <span>${timenow}</span>
                                </div>
                            </div>
                        </div>
                

<!--                     r.questionImgDtoList.slice(0,1).forEach(e =>{-->

                        
                              <div class="content-img-box">
                                <div class="content-img">
                                    <img src="/qnar/queImg?fileFullPath=${r.questionImgRoute + '/' + r.questionImgUuid + '_' + r.questionImgName}" alt="">
                                </div>
                            </div>
               </a>
            `;
    })

    textInput.html(text);

    let paginations = $('.list-pagestions');

    page.pagination(result,paginations)
    paginations.find('a').on('click', function (e) {
        e.preventDefault();
        const page = parseInt($(this).data('page'));
        list.listSearchDto(page, searchQnaBoardForm(),'qnar','qnalist', showQnaBoardList);
    });
}




