import * as list from "./module/list.js";
import * as page from "./module/page.js";
import * as date from "./module/form.js";



$(document).ready(function (){
    let userId = $('.reviewlistmain').data('userid');
    list.list(0,userId,'mypgs','myreviewlist',showList);

    //날짜 포맷 변경
    let regDateString = $('#reviewRegisterdata').val();

    let formattedDateReg = date.formatDate(regDateString);
    $('.reviewRd').text(formattedDateReg);


})

function showList(result){

    let text =``;
    let textinput =$('.list-contents-box');


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
    }else{

        result.content.forEach(r =>{

            text +=`
                     <div class="list-content" data-id="">
                        <div class="list-content-title">
                            <span class="reviewTitle">${r.title}</span>
                            <span id="reviewRegisterdata"  class="reviewRd" value="${r.reviewRd}"></span>
                        </div>
                        <div class="orderproduct">${r.goodsName}</div>
                        <div class="list-content-content">
                            <div  class="list-content-content-detail">
                              ${r.content}
                            </div>
                        </div>    
                        <div class="list-content-etc">
                            
                            <div class="list-content-rating">
                                <span>평점</span>
                        `;
            if(r.rating == 1) {
                text +=`<span class="rating-count">★</span>`;
            }else if(r.rating == 2){
                text +=`<span class="rating-count">★★</span>`;
            }else if(r.rating ==3){
                text +=`<span class="rating-count">★★★</span>`;
            }else if(r.rating == 4){
                text +=`<span class="rating-count">★★★★</span>`;
            }else if(r.rating == 5){
                text +=`<span class="rating-count">★★★★</span>`;
            }

            text +=`</div>
                            <div class="list-remove">
                                <div class="list-rmove-btn">
                                    <button type="submit" class="rebtn" data-id = "${r.id}">삭제</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    `;
        })

    }

    textinput.html(text);

    let paginations = $('.pagination-ul');
    page.pagination(result, paginations)
    paginations.find('a').on('click', function (e) {
        e.preventDefault();
        const page = parseInt($(this).data('page'));
        let userId = $('.reviewlistmain').data('userid');
        list.list(page,userId,'mypgs','myreviewlist',showList);

    });
}





//클릭시 해당 리뷰 삭제 (비동기 succes에 넣기)
$(document).on('click','.rebtn',function(){
    let ids = $(this).data('id');
    if(confirm("정말 삭제하시겠습니까?")){
        let userId = $('.reviewlistmain').data('userid');
        $.ajax({
            url: `/mypgs/deletereview/${ids}`,
            type: 'post',
            success: function () {
                window.location.href ="/mypg/myreviewpage"+ userId;

            },
            error: function (a, b, c) {
                console.error(c);
            }
        });

    }else{
        window.location.href='';
    }
})



