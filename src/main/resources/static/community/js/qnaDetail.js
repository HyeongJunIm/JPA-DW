import * as reply from './module/reply.js';
import * as date from './module/form.js';



const slides = document.querySelector('.slider'); //전체 슬라이드 컨테이너
const slideImg = document.querySelectorAll('.slider li'); //모든 슬라이드들
let currentIdx = 0; //현재 슬라이드 index
const slideCount = slideImg.length; // 슬라이드 개수
const prev = document.querySelector('.prev'); //이전 버튼
const next = document.querySelector('.next'); //다음 버튼
const slideWidth = 300; //한개의 슬라이드 넓이
// const slideMargin = 100; //슬라이드간의 margin 값

//전체 슬라이드 컨테이너 넓이 설정
slides.style.width = slideWidth * slideCount + 'px';

function moveSlide(num) {
    slides.style.left = -num * 300 + 'px';
    currentIdx = num;
}

prev.addEventListener('click', function () {
    /*첫 번째 슬라이드로 표시 됐을때는
    이전 버튼 눌러도 아무런 반응 없게 하기 위해
    currentIdx !==0일때만 moveSlide 함수 불러옴 */

    if (currentIdx !== 0) moveSlide(currentIdx - 1);
});

next.addEventListener('click', function () {
    /* 마지막 슬라이드로 표시 됐을때는
    다음 버튼 눌러도 아무런 반응 없게 하기 위해
    currentIdx !==slideCount - 1 일때만
    moveSlide 함수 불러옴 */
    if (currentIdx !== slideCount - 1) {
        moveSlide(currentIdx + 1);
    }
});



//수정 페이지 이동
$('.modify-btn').on('click',function (){
    let questionId = $(this).data('questionid');


    window.location.href="/qna/modify/" + questionId;

})

$('.remove-btn').on('click',function (){
    let questionId =$(this).data('questionid');


    window.location.href="/qna/delete/" + questionId;

})



$(document).ready(function () {


    reply.limitText( $('#questioncomment'),'.comment-length-Check' )
    //댓글 목록
    showQuestionReplyList(questionId,replyList);


});

$('.reply-submit').on('click',function (){
    let questionComment = $('#questioncomment').val();

    if(!questionComment){
        alert("댓글 내용을 입력해 주세요");
        return false;

    }

    if(reply.getTextLength(questionComment)>200){
        alert("200자 내로 작성해 주세요")
        return;
    }


    registerReply();

    $('#questioncomment').val('')
    $('.comment-length-Check').text('');

})





//댓글 등록
function registerReply(){
    let questionCommentContent = $('#questioncomment').val();
    let questionId=$('#questionId').val();
    let userId = $('#userId').val();


    $.ajax({
        url : '/qnar/questionReply',
        type: 'post',
        data : {
            questionCommentContent : questionCommentContent,
            questionId : questionId,
            userId : userId

        },
        success : function (){

            showQuestionReplyList(questionId, replyList)

        },error : function (a,b,c){
            console.log(c);
        }
    })

}

let questionId=$('#questionId').val();
let userId = $('#userId').val();

function showQuestionReplyList(questionId,callback){
    $.ajax({
        url : `/qnar/showQuestionReplyList/${questionId}`,
        type:  'get',
        dataType : 'json',
        success : function(result){
            console.log(result);
            if(result){
                callback(result);
            }

        },error : function(a,b,c){
            console.log(c);
        }
    })

}

function replyList(result){
    let text='';
    let textInput = $('.reply-list');

    result.forEach(r => {

        text += `
                <div class="reply">
                        <div class="id-and-date">
                            <div class="reply-info">
        `;

        if(r.userImgId ==null){
            text += ` <div class="reply-img"><img src="/img/dogImg02.jpg" alt=""></div>`;
        }else {
            text += `<div class="reply-img"><img src="/qnar/qnaUserImg?userImgPath=${r.userImgPath +'/' + r.userImgUuid +'_' + r.userImgName}" alt=""></div>`
        }

        if(r.userNickName == null){
            text += `<div class="reply-id">${r.userAccount}</div>`;
        }else{
            text +=`<div class="reply-id">${r.userNickName}</div>`;
        }
        text += `
                 </div>
                            <div class="reply-date"> ${reply.timeForToday(r.questionCommentRd) + (r.questionCommentRd == r.questionCommentMd ? ' 작성' : ' 수정')}</div>
                        </div>
                        <div class="reply-content-n-btns">
                            <div class="reply-content">${r.questionCommentContent}</div>
        
                     `;

        if(r.userId == userId){
            text+= `
                        <div class="reply-btns">
                                <div class="update-reply"><a href="" data-questioncommentid="${r.id}">수정</a></div>
                                <div class="delete-reply"><a href="" data-questioncommentid="${r.id}">삭제</a></div>
                            </div>
                `;

        }

        text += `
                        </div>
                    </div>
            
            `;


    })


    textInput.html(text);

}


// 댓글 수정 삭제 버튼 팝업
$('.reply-list').on('click', '.reply-section-btns', function () {
    let replyBtnBox = $(this).closest('.reply-content-n-btns').find('.reply-btns');
    $('.reply-btns').addClass('none');
    replyBtnBox.toggleClass('none');



});


// $('body').click(function (e) {
//     if ($(e.target).hasClass('reply-section-btns')) {
//         return;
//     }
//     if (!$('.reply-btns').has(e.target).length) {
//         $('.reply-btns').addClass('none');
//     }
// });

//삭제버튼
$('.reply-list').on('click', '.delete-reply a', function (e){

    e.preventDefault();

    let questioncommentId = $(this).closest('.reply-btns').find('.delete-reply a').data('questioncommentid');

    if(confirm("삭제하시겠습니까?")){

        $.ajax({
            url : '/qnar/questionCommentReplyDelete/'+questioncommentId,
            type:'delete',
            success : function (){
                showQuestionReplyList(questionId,replyList);
            }
        })
    }
})



//수정하기
$('.reply-list').on('click', '.update-reply a', function (e) {

    e.preventDefault();
    console.log(e.preventDefault());


    let letterLimit = $(this).closest('.reply').find('.reply-date');
    let modifyContentPopUp = $(this).closest('.reply-content-n-btns').find('.reply-content');
    let btnBox = $(this).closest('.reply-content-n-btns').find('.reply-btns');




    letterLimit.replaceWith(
        `
          <div class=modify-limit>
                <span class="textLengthCheck">
                ${reply.getTextLength(modifyContentPopUp.text()) + ' / 200 '}
                </span>
          </div>
          
        `
    );

    modifyContentPopUp.replaceWith(
        ` <div class="modify-box">
            <textarea class="modify-reply-content">${modifyContentPopUp.text()}</textarea>
            <button type="button" class="modify-reply-btn">수정</button>
          </div>
        `
    );

    //수정, 삭제버튼 감추기
    btnBox.css('display', 'none')

    //수정창 글자 수 실시간 카운팅
    reply.limitModifyText('.reply-list', '.modify-reply-content', '.textLengthCheck' ,'.modify-limit');

})

$('.reply-list').on('click', '.modify-reply-btn', function (){

    let commentId = $(this).closest('.reply-content-n-btns').find('.update-reply a').data('questioncommentid');
    let modifyContentVal = $(this).closest('.modify-box').find('.modify-reply-content').val();

    if(!modifyContentVal){
        alert("댓글 내용을 입력해주세요")
        return;
    }

    if(reply.getTextLength(modifyContentVal)>200){
        alert("200자 이내로 작성해주세요")
        return;
    }

    $.ajax({

        url:'/qnar/questionReply',
        type:'patch',
        data : {
            id : commentId,
            questionCommentContent : modifyContentVal,

        },
        success : function (){
            showQuestionReplyList(questionId,replyList);
        },error : function (a,b,c){
            console.error(c);
        }

    })

})

