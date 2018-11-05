$(function () {
    $('.btn,.load-btn').on('click', function () {
        var $this = $(this);
        var loadingText = $this.data('loading-text');
        if ($this.is('button'))
            $this.prop("disabled", true);
        else if ($this.is('a')) {
            $this.addClass('disabled');
            $this.prop("tabindex", -1);
        }
        $this.html('<i class="fa fa-circle-o-notch fa-spin fa-fw"></i>' + (loadingText? '&nbsp;' + loadingText : ''));
    })
});