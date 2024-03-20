const confirmDelete = (id, message, successUrl) => {
    if (confirm(message)) {
        const request = jsRoutes.controllers.ArticlesController.delete(id);
        fetch(request.url, {
            method: request.method,
        })
            .then(() => {
                location.href = successUrl;
            })
            .catch(error => {
                console.log(error);
            });
    }
};
