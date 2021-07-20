package ru.plantarum.core.uploading.excel;

public enum EntityFields {

    PASS {
        @Override
        public String toString() {
            return "Пропустить";
        }
    },

    PRICE_OUT {
        @Override
        public String toString() {
            return "Цена выход";
        }
    },

    PRICE_IN {
        @Override
        public String toString() {
            return "Цены вход";
        }
    },

    EAN13 {
        @Override
        public String toString() {
            return "Штрих код";
        }
    },
    //IMPORTANT???
    NUMBER_IN_PACK {
        @Override
        public String toString() {
            return "Кол-во в упаковке";
        }
    },
    //IMPORTANT
    PRODUCT_NAME {
        @Override
        public String toString() {
            return "Наименование продукта";
        }
    },

    TRADEMARK {
        @Override
        public String toString() {
            return "Торговая марка";
        }
    },

    ORGAN_TYPE {
        @Override
        public String toString() {
            return "Тип органа";
        }
    }
}
