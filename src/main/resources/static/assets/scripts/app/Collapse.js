export default class Collapse {

    constructor(el) {
        this.el = el;

        this.id = this.el.id;
        this.name = this.el.querySelector('summary').textContent;

        /**
         * @type {Collapse[]}
         */
        this.items = [];

        this.el.querySelectorAll(':scope > .content > details').forEach(node => {
            this.items.push(new Collapse(node));
        });
    }

    /**
     * @param {string} filter
     * @return {boolean}
     */
    applyFilter(filter) {
        const isNameMatching = this.name.toLowerCase().indexOf(filter.toLowerCase()) > -1;

        if (isNameMatching || filter === '') {
            this.show();
            this.items.forEach(item => {
                item.show();
                item.collapse();
            });
            this.collapse();
            return true;
        }

        if (this.items.length) {
            let hasMatched = false;
            for (let item of this.items) {
                if (item.applyFilter(filter)) {
                    hasMatched = true;
                }
            }

            if (hasMatched) {
                this.show();
                this.expand();
            } else {
                this.hide();
                this.collapse();
            }
            return hasMatched;
        }

        this.hide();
        this.collapse();
        return false;
    }

    hide() {
        this.el.classList.add('hidden')
    }

    show() {
        this.el.classList.remove('hidden')
    }

    collapse() {
        if (this.el.tagName === "DETAILS") {
            this.el.open = false;
        }
    }

    expand() {
        if (this.el.tagName === "DETAILS") {
            this.el.open = true;
        }
    }
}
