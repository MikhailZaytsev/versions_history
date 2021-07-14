package ru.plantarum.core.uploading.response;

public enum EntityMessage {
    PRODUCT {
        @Override
        public String toString() {
            return "Продукты";
        }
    },

    BARE_CODE {
        @Override
        public String toString() {
            return "Штрих-коды";
        }
    },

    PRICE_BUY_PRELIMINARILY {
        @Override
        public String toString() {
            return "Цена вход";
        }
    },

    PRICE_SALE {
        @Override
        public String toString() {
            return "Цена выход";
        }
    }
}
