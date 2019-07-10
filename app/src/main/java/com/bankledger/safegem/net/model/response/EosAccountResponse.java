package com.bankledger.safegem.net.model.response;

import java.util.List;

import com.google.gson.JsonObject;


public class EosAccountResponse {

    public String account_name;

    public String privileged;

    public String last_code_update;

    public String created;

    public Long ram_quota;

    public Long net_weight;

    public Long cpu_weight;

    public NetLimit net_limit;

    public CpuLimit cpu_limit;

    public Long ram_usage;

    public List<JsonObject> permissions;

    public class CpuLimit {

        public Long used;

        public Long available;

        public Long max;

    }

    public class NetLimit {

        public Long used;

        public Long available;

        public Long max;
    }
}
